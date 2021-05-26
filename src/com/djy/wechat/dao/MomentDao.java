package com.djy.wechat.dao;

import com.djy.wechat.dao.annotation.Query;
import com.djy.wechat.dao.annotation.Result;
import com.djy.wechat.dao.annotation.ResultType;
import com.djy.wechat.entity.po.Moment;

import java.util.List;

/**
 * @description 负责朋友圈的CRUD
 */
public interface MomentDao extends BaseDao {
    String TABLE = "moment";
    String ALL_FIELD = "owner_id,content,photo,time,love,remark,view," + BASE_FIELD;

    /**
     * 通过朋友圈id查询一个朋友圈
     * @param id 朋友圈id
     * @name geMomentById
     */
    @Result(entity = Moment.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where id = ? ")
    Moment getMomentById(Object id);

    /**
     * 通过用户id和状态查询一个朋友圈
     * @param ownerId 用户id
     * @param stauts 状态
     */
    @Result(entity = Moment.class, returns = ResultType.OBJECT)
    @Query(value = "select " + ALL_FIELD + " from " + TABLE + " where owner_id = ? and status = ? ")
    Moment getMomentByOwnerIdAndStatus(Object ownerId, Object stauts);


    /**
     * 通过用户id逆序查询所有自己发布的朋友圈
     * @param ownerId 用户id
     * @param limit  每页查询记录数
     * @param offset 起始记录数
     * @name listMyMomentByOwnerIdDesc
     */
    @Result(entity = Moment.class, returns = ResultType.LIST)
    @Query("select " + ALL_FIELD + " from " + TABLE + " where owner_id = ?  order by time desc limit ? offset ?  ")
    List<Moment> listMyMomentByOwnerIdDesc(Object ownerId, int limit, int offset);

    /**
     * 通过用户id查询所有自己发布的朋友圈
     * @param ownerId 用户id
     * @param limit  每页查询记录数
     * @param offset 起始记录数
     * @name listMyMomentByOwnerId
     */
    @Result(entity = Moment.class, returns = ResultType.LIST)
    @Query("select " + ALL_FIELD + " from " + TABLE + " where owner_id = ?  order by time limit ? offset ?  ")
    List<Moment> listMyMomentByOwnerId(Object ownerId, int limit, int offset);


    /**
     * 通过用户id查询所有自己发布的朋友圈中的图片
     * @param ownerId 用户id
     * @param limit  每页查询记录数
     * @param offset 起始记录数
     * @name loadPhoto
     */
    @Result(entity = Moment.class, returns = ResultType.LIST)
    @Query("select photo from " + TABLE + " where owner_id = ?  order by time desc limit ? offset ?  ")
    List<Moment> listPhoto(Object ownerId, int limit, int offset);
}
