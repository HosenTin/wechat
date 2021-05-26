package com.djy.wechat.service.impl;

import com.djy.wechat.dao.FriendDao;
import com.djy.wechat.dao.MomentDao;
import com.djy.wechat.dao.NewsDao;
import com.djy.wechat.dao.UserDao;
import com.djy.wechat.exception.DaoException;
import com.djy.wechat.exception.ServiceException;
import com.djy.wechat.proxy.DaoProxyFactory;
import com.djy.wechat.entity.builder.MomentVOBuilder;
import com.djy.wechat.entity.dto.ServiceResult;
import com.djy.wechat.entity.po.Friend;
import com.djy.wechat.entity.po.Moment;
import com.djy.wechat.entity.po.News;
import com.djy.wechat.entity.po.User;
import com.djy.wechat.entity.vo.MomentVO;
import com.djy.wechat.service.MomentService;
import com.djy.wechat.service.constants.ServiceMessage;
import com.djy.wechat.service.constants.Status;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

import static com.djy.wechat.util.StringUtils.toLegalText;

/**
 * @description 负责提供朋友圈相关的服务
 */
public class MomentServiceImpl implements MomentService {

    private final MomentDao momentDao = (MomentDao) DaoProxyFactory.getInstance().getProxyInstance(MomentDao.class);
    private final NewsDao newsDao = (NewsDao) DaoProxyFactory.getInstance().getProxyInstance(NewsDao.class);
    private final UserDao userDao = (UserDao) DaoProxyFactory.getInstance().getProxyInstance(UserDao.class);
    private final FriendDao friendDao = (FriendDao) DaoProxyFactory.getInstance().getProxyInstance(FriendDao.class);

    /**
     * 插入一条朋友圈
     * @param moment 朋友圈
     * @name insertMoment
     */
    @Override
    public ServiceResult insertMoment(Moment moment) {
        StringBuilder message = new StringBuilder();
        try {
            //判空
            if (moment == null) {
                throw new ServiceException(ServiceMessage.NOT_NULL.message);
            }
            //检查长度
            if(moment.getContent().length()>800){
                return new ServiceResult(Status.ERROR, ServiceMessage.CONTENT_TOO_LONG.message, moment);
            }
            //检查内容
            if (!isValidContent(moment.getContent())) {
                message.append(ServiceMessage.CONTENT_ILLEGAL.message);
            }
            if (isPoliticsContent(moment.getContent())) {
                return new ServiceResult(Status.ERROR, ServiceMessage.CONTENT_POLITICS.message, moment);
            }
            //过滤非法字符
            moment.setContent(toLegalText(moment.getContent()));
            //插入数据库
            //先把状态值设置为1，插入后查出状态为1的，返回给前端，并且对News表插入动态记录之后，再更新状态为0
            moment.setStatus(1);
            if (momentDao.insert(moment) != 1) {
                return new ServiceResult(Status.ERROR, ServiceMessage.PLEASE_REDO.message, moment);
            }
            //news表用于点击查看朋友圈显示的信息
            //获取朋友圈记录[新增朋友圈]，返回id值,获取数据库给moment生成自增的id
            moment = momentDao.getMomentByOwnerIdAndStatus(moment.getOwnerId(), 1);
            //查找用户的所有朋友，给他们插入news表记录
            List<Friend> friendList = friendDao.listByUserId(moment.getOwnerId());
            for (Friend friend : friendList) {
                //news表 —— 查看朋友圈时显示(从news表查询出来，关联朋友圈的id获得moment对象[内容、点赞数、评论数...]，朋友圈属性表)
                News news = new News();
                //朋友圈自增的id
                news.setMomentId(moment.getId());
                //好友的id
                news.setUserId(friend.getFriendId());
                newsDao.insert(news);
            }
            //给用户自己插入news记录(自己看)
            News news = new News();
            //朋友圈自增的id
            news.setMomentId(moment.getId());
            //自己的id
            news.setUserId(moment.getOwnerId());
            newsDao.insert(news);
            //创建一个新对象用于更新状态为0，表示该朋友圈处理完毕
            Moment updateStatus = new Moment();
            updateStatus.setId(moment.getId());
            updateStatus.setStatus(0);
            momentDao.update(updateStatus);
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, moment);
        }
        return new ServiceResult(Status.SUCCESS, message.append(ServiceMessage.POST_MOMENT_SUCCESS.message).toString(), moment);
    }

    /**
     * 给好友双方初始化朋友圈，互相添加动态
     * @param friend 好友
     * @name initNews
     */
    @Override
    public ServiceResult initNews(Friend friend) {
        if(friend==null|friend.getFriendId()==null||friend.getUserId()==null){
            return new ServiceResult(Status.ERROR, ServiceMessage.PARAMETER_NOT_ENOUGHT.message, null);
        }
        try{
            //通过好友表的用户id来获得用户的所有朋友圈
            List<Moment> momentList = momentDao.listMyMomentByOwnerIdDesc(friend.getUserId(), 1000, 0);
            for (Moment moment : momentList) {
                News news = new News();
                //设置值
                news.setMomentId(moment.getId());
                //此时news表里的用户id为朋友id
                news.setUserId(friend.getFriendId());
                //新增news表信息
                newsDao.insert(news);
            }
            //通过好友表的好友id来获得好友的所有朋友圈
            momentList =momentDao.listMyMomentByOwnerIdDesc(friend.getFriendId(), 1000, 0);
            for (Moment moment : momentList) {
                News news = new News();
                //设置值
                news.setMomentId(moment.getId());
                //此时news表里的用户id为用户id
                news.setUserId(friend.getUserId());
                newsDao.insert(news);
            }}catch (DaoException e){
                e.printStackTrace();
                return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, null);
            }
            return new ServiceResult(Status.SUCCESS,null,null);
    }

    /**
     * 删除一条朋友圈
     * @param momentId 朋友圈id
     * @name removeMoment
     */
    @Override
    public ServiceResult removeMoment(BigInteger momentId) {
        try {
            //判空
            if (momentId == null) {
                throw new ServiceException(ServiceMessage.NOT_NULL.message);
            }
            //检查是否存在
            if (momentDao.getMomentById(momentId) == null) {
                return new ServiceResult(Status.ERROR, ServiceMessage.NOT_FOUND.message, momentId);
            }
            //删除
            Moment moment = new Moment();
            moment.setId(momentId);
            if (momentDao.delete(moment) != 1) {
                return new ServiceResult(Status.ERROR, ServiceMessage.PLEASE_REDO.message, moment);
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, momentId);
        }
        return new ServiceResult(Status.SUCCESS, ServiceMessage.OPERATE_SUCCESS.message, momentId);
    }

    /**
     * 查询一个用户所发的所有朋友圈
     * @param userId 用户id
     * @param page   页数
     * @name listNews
     */
    @Override
    public ServiceResult listMyMoment(BigInteger userId, int page) {
        //判空
        if (userId == null) {
            throw new ServiceException(ServiceMessage.NOT_NULL.message);
        }

        List<MomentVO> momentVOList = new LinkedList<>();
        //根据页数信息生成查询参数
        int limit = 10;
        int offset = (page - 1) * limit;
        if (offset < 0) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PAGE_INVALID.message, null);
        }
        try {
            List<Moment> momentList = momentDao.listMyMomentByOwnerIdDesc(userId, limit, offset);
            if (momentList == null || momentList.size() == 0) {
                return new ServiceResult(Status.SUCCESS, ServiceMessage.NO_MOMENT.message, momentList);
            }
            User user = userDao.getUserById(userId);
            //将朋友圈和用户信息转化成朋友圈视图层对象
            for (Moment moment : momentList) {
                News news = null;
                try {
                    news = newsDao.getNewsByMomentIdAndUserId(moment.getId(), userId);
                } catch (DaoException e) {
                    e.printStackTrace();
                }
                if (news == null) {
                    return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, null);
                }
                toMomentVOObject(momentVOList, news, moment, user);
            }
        } catch (DaoException e) {
            e.printStackTrace();
        }
        return new ServiceResult(Status.SUCCESS,null, momentVOList);
    }

    /**
     * 查询一个用户可见的所有朋友圈，包括自己的和朋友的
     * @param userId 用户id
     * @param page   页数
     * @name listNews
     */
    @Override
    public ServiceResult listNews(BigInteger userId, int page) {
        //根据页数信息生成查询参数
        int limit = 10;
        int offset = (page - 1) * limit;
        if (offset < 0) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PAGE_INVALID.message, null);
        }
        List<MomentVO> momentVOList = new LinkedList<>();
        try {
            List<News> newsList;
            if (userId == null) { // 管理员
                newsList = newsDao.listAllNews(limit,offset);
            }else {
                newsList = newsDao.listNewsByUserId(userId, limit, offset);
            }
            //查找朋友圈动态
            if (newsList == null || newsList.size() == 0) {
                if(page==1){
                    return new ServiceResult(Status.SUCCESS, ServiceMessage.NO_NEWS.message, momentVOList);
                }
                return new ServiceResult(Status.SUCCESS, ServiceMessage.NO_MORE.message, momentVOList);
            }
            //根据动态中的朋友圈id获取朋友圈数据
            for (News news : newsList) {
                Moment moment = momentDao.getMomentById(news.getMomentId());
                //获取朋友圈的发布者的用户信息
                User user = userDao.getUserById(moment.getOwnerId());
                if(user==null){
                    return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, momentVOList);
                }
                //将朋友圈和动态信息转化成朋友圈视图层对象
                toMomentVOObject(momentVOList, news, moment, user);
                //统计浏览量
                if(!news.getViewed()){
                    moment.setView(moment.getView()+1L);
                    momentDao.update(moment);
                    news.setViewed(true);
                    newsDao.update(news);
                }
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, momentVOList);
        }
        return new ServiceResult(Status.SUCCESS,null, momentVOList);
    }

    /**
     * 更新一个用户对一条朋友圈的点赞状态
     * @param userId   用户id
     * @param momentId 朋友圈id
     * @name love
     */
    @Override
    synchronized  public ServiceResult love(BigInteger userId, BigInteger momentId) {
        if (userId == null || momentId == null) {
            throw new ServiceException(ServiceMessage.NOT_NULL.message);
        }
        News news;
        try {
            news = newsDao.getNewsByMomentIdAndUserId(momentId, userId);
            //检查news是否存在
            if (news == null) {
                return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, null);
            }
            Moment moment = momentDao.getMomentById(news.getMomentId());
            if (moment == null) {
                return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, null);
            }
            //从非点赞到点赞状态，该朋友圈点赞数加一,否则减一
            moment.setLove(!news.getLoved() ? moment.getLove() + 1 : moment.getLove() - 1);
            momentDao.update(moment);
            //修改状态,如果是ture则改为false,是false则改为ture
            news.setLoved(!news.getLoved());
            newsDao.update(news);
        } catch (DaoException e) {
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, null);
        }
        return new ServiceResult(Status.SUCCESS,null, news.getLoved());
    }

    /**
     * 查询一个用户朋友圈中的所有图片
     * @param userId 用户id
     * @param page   页数
     * @name loadPhoto
     */
    @Override
    public ServiceResult listPhoto(BigInteger userId, int page) {
        //根据页数信息生成查询参数
        int limit = 10;
        int offset = (page - 1) * limit;
        if (offset < 0) {
            return new ServiceResult(Status.ERROR, ServiceMessage.PAGE_INVALID.message, null);
        }
        //判空
        if (userId == null) {
            throw new ServiceException(ServiceMessage.NOT_NULL.message);
        }
        List<String> photoList = new LinkedList<>();
        try {
                List<Moment> momentList = momentDao.listPhoto(userId,limit,offset);
                if(momentList==null||momentList.size()==0){
                    return new ServiceResult(Status.ERROR, ServiceMessage.NO_MOMENT.message, null);
                }
            for (Moment moment:momentList) {
                photoList.add(moment.getPhoto());
            }
        } catch (DaoException e) {
            e.printStackTrace();
            return new ServiceResult(Status.ERROR, ServiceMessage.DATABASE_ERROR.message, photoList);
        }
        return new ServiceResult(Status.SUCCESS, null, photoList);
    }

    /**
     * 建造一个朋友圈试图层对象集合，需要一个持久化对象集合，和一条动态记录，和该用户的信息
     * @param momentVOList 试图层对象集合
     * @param news         动态记录
     * @param moment       朋友圈
     * @param user         该用户的信息
     * @name toMomentVOObject
     */
    private void toMomentVOObject(List<MomentVO> momentVOList, News news, Moment moment, User user) {
        MomentVO momentVO = new MomentVOBuilder().setContent(moment.getContent()).setUserId(moment.getOwnerId())
                .setId(moment.getId()).setRemark(moment.getRemark()).setShare(moment.getShare()).setUserName(user.getName())
                .setView(moment.getView()).setCollect(moment.getCollect()).setLove(moment.getLove()).setUserPhoto(user.getPhoto())
                .setViewed(news.getViewed()).setPhoto(moment.getPhoto())
                .setLoved(news.getLoved()).setTime(moment.getTime()).build();
        momentVOList.add(momentVO);
    }

    /**
     * 检查一段内容是否合法
     * @param content 朋友圈内容
     */
    private boolean isValidContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        //如果内容经过过滤后与原来不一样，说明含有非法内容
        String legalText = toLegalText(content);
        if (content.equals(legalText)) {
            return true;
        }
        return false;
    }

    /**
     * 检查一段内容是否包含政治敏感话题
     * @param content
     * @return
     */
    private boolean isPoliticsContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            return false;
        }
        if (content.contains("收复台湾")) {
            return true;
        }
        return false;
    }
}
