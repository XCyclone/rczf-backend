package com.example.spba.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.spba.dao.CommunityInfoMapper;
import com.example.spba.domain.dto.CommunityInfoQuery;
import com.example.spba.domain.entity.CommunityInfo;
import com.example.spba.service.CommunityInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional(rollbackFor = Exception.class)
public class CommunityInfoServiceImpl implements CommunityInfoService {
    // 继承 ServiceImpl 后，已经拥有了 save (新增), getById, updateById, removeById 等方法

    @Autowired
    private CommunityInfoMapper communityInfoMapper;

    /**
     * 依据项目名称查询项目列表（支持分页）
     */
//    @Override
    public List<Map<String,Object>>  selectCommunityInfoListByCommunityName(String communityName) {
        if (communityName == null || "".equals(communityName)) {
            return communityInfoMapper.selectAll();

        }else {
            return communityInfoMapper.selectCommunityInfoList(null,communityName);

        }
    }
    
    /**
     * 分页查询小区列表
     */
    @Override
    public IPage<CommunityInfoQuery> selectCommunityInfoListByCommunityNameWithPage(IPage<CommunityInfoQuery> page, String communityName) {
        if (communityName == null || "".equals(communityName)) {
            // 调用自定义的分页查询方法
            return communityInfoMapper.selectCommunityInfoListWithPage(page, null);
        } else {
            return communityInfoMapper.selectCommunityInfoListWithPage(page, communityName);
        }
    }

//
//    public AjaxResult fileSave(MultipartFile[] communityfiles, MultipartFile[] zerobedroomlayout, MultipartFile[] zerobedroomphoto, MultipartFile[] onebedroomlayout, MultipartFile[] onebedroomphoto, MultipartFile[] twobedroomlayout, MultipartFile[] twobedroomphoto, MultipartFile[] threebedroomlayout, MultipartFile[] threebedroomphoto, CommunityInfo communityInfo) {
//        if (communityfiles != null && communityfiles.length > 0) {
//            //小区图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(communityfiles, "community",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取小区图片组 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setCommunityPicGroupId(picGroupId);
//
//        }
//        if (zerobedroomlayout != null && zerobedroomlayout.length > 0) {
//            //开间户型图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(zerobedroomlayout, "house/layout",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取图片 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setZerobedroomLayoutPicId(picGroupId);
//
//        }
//        if (zerobedroomphoto != null && zerobedroomphoto.length > 0) {
//            //开间实景图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(zerobedroomphoto, "house/photo",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取图片 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setZerobedroomPhotoPicId(picGroupId);
//
//        }
//        if (onebedroomlayout != null && onebedroomlayout.length > 0) {
//            //1 居户型图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(onebedroomlayout, "house/layout",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取图片 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setOnebedroomLayoutPicId(picGroupId);
//
//        }
//        if (onebedroomphoto != null && onebedroomphoto.length > 0) {
//            //1 居实景图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(onebedroomphoto, "house/photo",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取图片 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setOnebedroomPhotoPicId(picGroupId);
//
//        }
//        if (twobedroomlayout != null && twobedroomlayout.length > 0) {
//            //一居户型图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(twobedroomlayout, "house/layout",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取图片 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setTwobedroomLayoutPicId(picGroupId);
//
//        }
//        if (twobedroomphoto != null && twobedroomphoto.length > 0) {
//            //一居实景图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(twobedroomphoto, "house/photo",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取图片 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setTwobedroomPhotoPicId(picGroupId);
//
//        }
//        if (threebedroomlayout != null && threebedroomlayout.length > 0) {
//            //一居户型图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(threebedroomlayout, "house/layout",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取图片 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setThreebedroomLayoutPicId(picGroupId);
//
//        }
//        if (threebedroomphoto != null && threebedroomphoto.length > 0) {
//            //一居实景图片存储
//            AjaxResult picSaveResult = fileService.saveMultipleFiles(threebedroomphoto, "house/photo",communityInfo.getLastUpdater());
//            if (picSaveResult.isError()){
//                return picSaveResult;
//            }
//            //获取图片 id
//            String picGroupId = picSaveResult.get("fileGroupId").toString();
//            communityInfo.setThreebedroomPhotoPicId(picGroupId);
//
//        }
//        return AjaxResult.success();
//    }
}
