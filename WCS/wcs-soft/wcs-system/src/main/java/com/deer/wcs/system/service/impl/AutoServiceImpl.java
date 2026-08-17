package com.deer.wcs.system.service.impl;

import com.deer.wcs.common.core.service.AbstractService;
import com.deer.wcs.common.exception.ServiceException;
import com.deer.wcs.common.utils.DateUtils;
import com.deer.wcs.system.dao.AutoMapper;
import com.deer.wcs.system.model.Auto;
import com.deer.wcs.system.model.AutoCriteria;
import com.deer.wcs.system.model.AutoDto;
import com.deer.wcs.system.service.AutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 当日自增长Service业务层处理
 *
 * @author deer
 * @date 2023-10-23
 */
@Service
public class AutoServiceImpl extends AbstractService<Auto, Integer> implements AutoService {

    @Autowired
    private AutoMapper autoMapper;

    /**
     * prefix : 前缀
     * suffix : 后缀
     * nowDate : 当前年月日  24 08 20
     * no : 当前序号
     *
     * @return Sting
     */
    @Override
    public String getDsPalletCode() {
        String prefix = "";
        String suffix = "";
        String nowDate = DateUtils.getyyMMdd() + "-";
        Integer no = getNext(5);
        return prefix + nowDate + frontCompWithZore(no, 3) + suffix;
    }

    @Override
    public String getReqCode() {
        String quanzhui = "";
        String now = DateUtils.getyyMMdd() + "-";
        Integer no = getNext(1);
        return quanzhui + now + frontCompWithZore(no, 10);
    }

    @Override
    public String getId() {
        String quanzhui = "";
        String now = DateUtils.getyyMMdd() + "";
        return quanzhui + now + getNext(2);
    }

    @Override
    public String getTaskNo() {
        String now = DateUtils.getyyMMdd() + "";
        Integer no = getNext(3);
        return now + frontCompWithZore(no, 4);
    }

    @Override
    public Long getTaskInfoId() {
        String now = DateUtils.getyyMMdd() + "";
        Integer no = getNext(4);
        return Long.parseLong(now + frontCompWithZore(no, 4));
    }


    @Override
    public Long getPathInfoId() {
        String now = DateUtils.getyyMMdd() + "";
        Integer no = getNext(5);
        return Long.parseLong(now + frontCompWithZore(no, 4));
    }

    @Override
    public Long getJobHandleId() {
        String now = DateUtils.getyyMMdd() + "";
        Integer no = getNext(6);
        return Long.parseLong(now + frontCompWithZore(no, 4));
    }

    @Override
    public Long getPathHandleId() {
        String now = DateUtils.getyyMMdd() + "";
        Integer no = getNext(7);
        return Long.parseLong(now + frontCompWithZore(no, 4));
    }

    @Override
    public String getHtCrnTaskNo() {
        String now = DateUtils.getMMdd() + "";
        Integer no = getNext(8);
        return now + frontCompWithZore(no, 4);
    }


    @Override
    public Integer getTodayTaskNo() {
        String now = DateUtils.getMMdd();
        Integer no = getNext(9);
        return Integer.parseInt(now+frontCompWithZore(no,4));
    }

    @Override
    public Long getJobInfoId() {
        String now = DateUtils.getyyMMdd() + "";
        Integer no = getNext(10);
        return Long.parseLong(now + frontCompWithZore(no, 4));
    }


    @Async
    void deleteLast(Integer type, Integer thisId) {
        autoMapper.deleteLast(type, thisId);
    }

    private Integer getNext(Integer type) {
        try {
            String now = DateUtils.getyyMMdd();
            Auto auto = autoMapper.getByDateAndType(now, type);
            if (auto == null) {
                auto = new Auto();
                auto.setNo(1);
                auto.setType(type);
                auto.setDate(now);
                super.save(auto);
            } else {
                auto.setId(null);
                auto.setNo(auto.getNo() + 1);
                super.save(auto);
            }
            deleteLast(type, auto.getId());
            return auto.getNo();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ServiceException("获取自增长失败！", 500);
        }
    }

    public static String frontCompWithZore(int sourceDate, int formatLength) {
        /*
         * 0 指前面补充零
         * formatLength 字符总长度为 formatLength
         * d 代表为正数。
         */
        String newString = String.format("%0" + formatLength + "d", sourceDate);
        return newString;
    }


    /**
     * 查询当日自增长
     *
     * @param id 当日自增长主键
     * @return 当日自增长
     */
    @Override
    public Auto selectAutoById(Integer id) {
        return autoMapper.selectAutoById(id);
    }

    /**
     * 查询当日自增长列表
     *
     * @param criteria
     * @return 当日自增长
     */
    @Override
    public List<AutoDto> findList(AutoCriteria criteria) {
        return autoMapper.findList(criteria);
    }

    /**
     * 新增当日自增长
     *
     * @param auto 当日自增长
     * @return 结果
     */
    @Override
    public int insertAuto(Auto auto) {
        return autoMapper.insertAuto(auto);
    }

    /**
     * 修改当日自增长
     *
     * @param auto 当日自增长
     * @return 结果
     */
    @Override
    public int updateAuto(Auto auto) {
        return autoMapper.updateAuto(auto);
    }

    /**
     * 批量删除当日自增长
     *
     * @param ids 需要删除的当日自增长主键
     * @return 结果
     */
    @Override
    public int deleteAutoByIds(Integer[] ids) {
        return autoMapper.deleteAutoByIds(ids);
    }

    /**
     * 删除当日自增长信息
     *
     * @param id 当日自增长主键
     * @return 结果
     */
    @Override
    public int deleteAutoById(Integer id) {
        return autoMapper.deleteAutoById(id);
    }
}
