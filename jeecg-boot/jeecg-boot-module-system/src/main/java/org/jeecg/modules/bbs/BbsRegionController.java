package org.jeecg.modules.bbs.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.bbs.BbsAuthController;
import org.jeecg.modules.bbs.entity.BbsClass;
import org.jeecg.modules.bbs.entity.BbsRegion;
import org.jeecg.modules.bbs.entity.BbsUserRecord;
import org.jeecg.modules.bbs.service.IBbsClassService;
import org.jeecg.modules.bbs.service.IBbsRegionService;
import org.jeecg.modules.bbs.service.impl.BbsUserRecordServiceImpl;
import org.jeecg.modules.bbs.utils.BbsAuthUtils;
import org.jeecg.modules.bbs.vo.BbsRegionPage;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: ??????
 * @Author: jeecg-boot
 * @Date: 2021-05-27
 * @Version: V1.0
 */
@Api(tags = "??????")
@RestController
@RequestMapping("/bbs/bbsRegion")
@Slf4j
public class BbsRegionController {
    @Autowired
    private IBbsRegionService bbsRegionService;
    @Autowired
    private IBbsClassService bbsClassService;
    @Autowired
    private BbsUserRecordServiceImpl bbsUserRecordService;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    private ISysDepartService sysDepartService;
    @Autowired
    private BbsAuthController bbsAuthController;
    @Autowired
    private BbsAuthUtils bbsAuthUtils;

    /**
     * ??????????????????
     *
     * @param bbsRegion
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "??????-??????????????????")
    @ApiOperation(value = "??????-??????????????????", notes = "??????-??????????????????")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(BbsRegion bbsRegion,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<BbsRegion> queryWrapper = QueryGenerator.initQueryWrapper(bbsRegion, req.getParameterMap());
        Page<BbsRegion> page = new Page<BbsRegion>(pageNo, pageSize);
        IPage<BbsRegion> pageList = bbsRegionService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * ??????
     *
     * @param bbsRegionPage
     * @return
     */
    @AutoLog(value = "??????-??????")
    @ApiOperation(value = "??????-??????", notes = "??????-??????")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody BbsRegionPage bbsRegionPage) {
        BbsRegion bbsRegion = new BbsRegion();
        BeanUtils.copyProperties(bbsRegionPage, bbsRegion);
        bbsRegionService.saveMain(bbsRegion, bbsRegionPage.getBbsClassList());
        return Result.OK("???????????????");
    }

    /**
     * ??????
     *
     * @param bbsRegionPage
     * @return
     */
    @AutoLog(value = "??????-??????")
    @ApiOperation(value = "??????-??????", notes = "??????-??????")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody BbsRegionPage bbsRegionPage) {
        BbsRegion bbsRegion = new BbsRegion();
        BeanUtils.copyProperties(bbsRegionPage, bbsRegion);
        BbsRegion bbsRegionEntity = bbsRegionService.getById(bbsRegion.getId());
        if (bbsRegionEntity == null) {
            return Result.error("?????????????????????");
        }
        bbsRegionService.updateMain(bbsRegion, bbsRegionPage.getBbsClassList());
        return Result.OK("????????????!");
    }

    /**
     * ??????id??????
     *
     * @param id
     * @return
     */
    @AutoLog(value = "??????-??????id??????")
    @ApiOperation(value = "??????-??????id??????", notes = "??????-??????id??????")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        bbsRegionService.delMain(id);
        return Result.OK("????????????!");
    }

    /**
     * ????????????
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "??????-????????????")
    @ApiOperation(value = "??????-????????????", notes = "??????-????????????")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.bbsRegionService.delBatchMain(Arrays.asList(ids.split(",")));
        return Result.OK("?????????????????????");
    }

    /**
     * ??????id??????
     *
     * @param id
     * @return
     */
    @AutoLog(value = "??????-??????id??????")
    @ApiOperation(value = "??????-??????id??????", notes = "??????-??????id??????")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        BbsRegion bbsRegion = bbsRegionService.getById(id);
        if (bbsRegion == null) {
            return Result.error("?????????????????????");
        }
        return Result.OK(bbsRegion);

    }

    /**
     * ??????id??????
     *
     * @param id
     * @return
     */
    @AutoLog(value = "??????????????????ID??????")
    @ApiOperation(value = "????????????ID??????", notes = "??????-?????????ID??????")
    @GetMapping(value = "/queryBbsClassByMainId")
    public Result<?> queryBbsClassListByMainId(@RequestParam(name = "id", required = true) String id) {
        List<BbsClass> bbsClassList = bbsClassService.selectByMainId(id);
        return Result.OK(bbsClassList);
    }

    /**
     * ??????excel
     *
     * @param request
     * @param bbsRegion
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, BbsRegion bbsRegion) {
        // Step.1 ??????????????????????????????
        QueryWrapper<BbsRegion> queryWrapper = QueryGenerator.initQueryWrapper(bbsRegion, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        //Step.2 ??????????????????
        List<BbsRegion> queryList = bbsRegionService.list(queryWrapper);
        // ??????????????????
        String selections = request.getParameter("selections");
        List<BbsRegion> bbsRegionList = new ArrayList<BbsRegion>();
        if (oConvertUtils.isEmpty(selections)) {
            bbsRegionList = queryList;
        } else {
            List<String> selectionList = Arrays.asList(selections.split(","));
            bbsRegionList = queryList.stream().filter(item -> selectionList.contains(item.getId())).collect(Collectors.toList());
        }

        // Step.3 ??????pageList
        List<BbsRegionPage> pageList = new ArrayList<BbsRegionPage>();
        for (BbsRegion main : bbsRegionList) {
            BbsRegionPage vo = new BbsRegionPage();
            BeanUtils.copyProperties(main, vo);
            List<BbsClass> bbsClassList = bbsClassService.selectByMainId(main.getId());
            vo.setBbsClassList(bbsClassList);
            pageList.add(vo);
        }

        // Step.4 AutoPoi ??????Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, "????????????");
        mv.addObject(NormalExcelConstants.CLASS, BbsRegionPage.class);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("????????????", "?????????:" + sysUser.getRealname(), "??????"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * ??????excel????????????
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// ????????????????????????
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                List<BbsRegionPage> list = ExcelImportUtil.importExcel(file.getInputStream(), BbsRegionPage.class, params);
                for (BbsRegionPage page : list) {
                    BbsRegion po = new BbsRegion();
                    BeanUtils.copyProperties(page, po);
                    bbsRegionService.saveMain(po, page.getBbsClassList());
                }
                return Result.OK("?????????????????????????????????:" + list.size());
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("??????????????????:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return Result.OK("?????????????????????");
    }


    // ****???????????????????????????****

    /**
     * ????????????
     *
     * @param bbsRegion
     * @param req
     * @return
     */
    @AutoLog(value = "??????-????????????")
    @ApiOperation(value = "??????-????????????", notes = "??????-????????????")
    @GetMapping(value = "/wise/mini/queryList")
    public Result<?> queryList(BbsRegion bbsRegion, HttpServletRequest req) {
        QueryWrapper<BbsRegion> queryWrapper = QueryGenerator.initQueryWrapper(bbsRegion, req.getParameterMap());
        Map<String, String[]> reqParameterMap = req.getParameterMap();
        queryWrapper.orderBy(true, true, "create_time");
        //??????fullName???????????????
        queryWrapper.orderBy(true, true, "convert(full_name USING gbk)");
        //????????????
        //queryWrapper.eq("is_private",0);
        List<BbsRegion> list = bbsRegionService.list(queryWrapper);

        for (BbsRegion region : list) {
            region.setRegionPeopleNum(bbsUserRecordService.lambdaQuery().eq(BbsUserRecord::getRegionCode, region.getRegionCode()).count());
        }
        list.sort(Comparator.comparing(BbsRegion::getScale).thenComparing(Comparator.comparing(BbsRegion::getRegionPeopleNum).reversed()));
        return Result.OK(list);
    }

    /**
     * ????????????
     *
     * @return
     */
    @AutoLog(value = "??????-??????regionCode??????????????????")
    @ApiOperation(value = "??????-??????regionCode??????????????????", notes = "??????-??????regionCode??????????????????")
    @GetMapping(value = "/wise/mini/queryByRegionCode")
    public Result<?> queryByRegionCode(@RequestParam(value = "regionCode") String regionCode) {
        BbsRegion one = bbsRegionService.lambdaQuery().eq(BbsRegion::getRegionCode, regionCode).one();
        return Result.OK(one);
    }


    /**
     * ??????-??????
     *
     * @param bbsRegion
     * @param username  ???????????????token????????????????????????????????????????????????shiro?????????????????????????????????????????????username
     * @return
     */
    @AutoLog(value = "??????-??????-???????????????????????????")
    @ApiOperation(value = "??????-??????", notes = "??????-??????-???????????????????????????")
    @PostMapping(value = "/wise/mini/switchRegion")
    public Result<?> switchRegion(@RequestBody BbsRegion bbsRegion, @RequestParam(name = "username", defaultValue = "") String username) {
        BbsUserRecord bbsUserRecord = new BbsUserRecord();

        String currentUsername = "";
        if (!"".equals(username)) {
            bbsUserRecord = bbsUserRecordService.lambdaQuery().eq(BbsUserRecord::getCreateBy, username).one();
            currentUsername = username;
        } else {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            bbsUserRecord = bbsUserRecordService.lambdaQuery().eq(BbsUserRecord::getCreateBy, sysUser.getUsername()).one();
            currentUsername = sysUser.getUsername();
        }
        bbsUserRecordService.lambdaUpdate().eq(BbsUserRecord::getCreateBy, currentUsername)
                .set(BbsUserRecord::getRegionCode, bbsRegion.getRegionCode())
                .set(BbsUserRecord::getSysOrgCode, bbsRegion.getSysOrgCode())
                .update();
        log.info("getMiNiTokenStorage:username=" + username);
        //????????????regionSwitchCount+1???region_switch_date????????????region_code?????????????????????region_fullname???????????????
        if (bbsUserRecord.getRegionCode().equals(bbsRegion.getRegionCode())) {
            return Result.OK("????????????????????????????????????");
        }
        SysUser userByName = sysUserService.getUserByName(currentUsername);

        sysUserService.addUserWithDepart(userByName, bbsRegion.getRegionDepartId());    //????????????????????????????????????,??????
        SysDepart sysDepartServiceById = sysDepartService.getById(bbsRegion.getRegionDepartId());
        sysUserService.updateUserDepart(userByName.getUsername(), sysDepartServiceById.getOrgCode());   //????????????????????????

        bbsAuthUtils.getMiNiStorageFromSql(userByName.getUsername());
        return Result.OK("??????????????????");
    }

    /**
     * ??????-??????
     *
     * @param bbsRegion
     * @param username  ???????????????token????????????????????????????????????????????????shiro?????????????????????????????????????????????username
     * @return
     */
    @AutoLog(value = "??????-??????-????????????????????????")
    @ApiOperation(value = "??????-??????", notes = "??????-??????")
    @PostMapping(value = "/wise/mini/switchRegionAddCount")
    public Result<?> switchRegionAddCount(@RequestBody BbsRegion bbsRegion, @RequestParam(name = "username", defaultValue = "") String username) {
        BbsUserRecord bbsUserRecord = new BbsUserRecord();

        String currentUsername = "";
        if (!"".equals(username)) {
            bbsUserRecord = bbsUserRecordService.lambdaQuery().eq(BbsUserRecord::getCreateBy, username).one();
            currentUsername = username;
        } else {
            LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            bbsUserRecord = bbsUserRecordService.lambdaQuery().eq(BbsUserRecord::getCreateBy, sysUser.getUsername()).one();
            currentUsername = sysUser.getUsername();
        }

        //????????????regionSwitchCount+1???region_switch_date????????????region_code?????????????????????region_fullname???????????????
        if (bbsUserRecord.getRegionCode().equals(bbsRegion.getRegionCode())) {
            return Result.OK("????????????????????????????????????");
        }

        bbsUserRecord.setRegionCode(bbsRegion.getRegionCode());
        bbsUserRecord.setSysOrgCode(bbsRegion.getSysOrgCode());
        bbsUserRecord.setRegionSwitchCount(bbsUserRecord.getRegionSwitchCount() + 1);
        bbsUserRecordService.setUserRecord(bbsUserRecord);

        SysUser userByName = sysUserService.getUserByName(currentUsername);
        sysUserService.addUserWithDepart(userByName, bbsRegion.getRegionDepartId());    //????????????????????????????????????,??????
        SysDepart sysDepartServiceById = sysDepartService.getById(bbsRegion.getRegionDepartId());
        sysUserService.updateUserDepart(userByName.getUsername(), sysDepartServiceById.getOrgCode());   //????????????????????????

        bbsAuthUtils.getMiNiStorageFromSql(userByName.getUsername());
        return Result.OK("??????????????????");
    }

    /**
     * BBS????????????
     *
     * @param bbsRegionPage
     * @return
     */
    @AutoLog(value = "??????-??????")
    @ApiOperation(value = "??????-??????", notes = "??????-??????")
    @PostMapping(value = "/wise/back/addRegion")
    public Result<?> addRegion(@RequestBody BbsRegionPage bbsRegionPage) {
        BbsRegion bbsRegion = new BbsRegion();
        BeanUtils.copyProperties(bbsRegionPage, bbsRegion);
        bbsRegionService.addRegion(bbsRegion, bbsRegionPage.getBbsClassList());
        return Result.OK("???????????????");
    }
    /**
     * ???????????????????????????
     *  ???????????????jdbc??????????????????&allowMultiQueries=true
     *  druid filters ??????wall, ???????????????
     * @return
     */
    //@AutoLog(value = "??????-???????????????????????????")
    //@ApiOperation(value = "??????-???????????????????????????", notes = "??????-???????????????????????????")
    //@PostMapping(value = "/wise//initAllRegionClass")
    //public Result<?> initAllRegionClass() {
    //    bbsRegionService.initAllRegionClass();
    //    return Result.OK("???????????????");
    //}


    /**
     * ??????
     * ??????????????????
     *
     * @param bbsRegionPage
     * @param bbsRegion
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "??????-??????")
    @ApiOperation(value = "??????-??????", notes = "??????-??????")
    @PostMapping(value = "/wise/back/add")
    public Result<?> addWiseBack(@RequestBody BbsRegionPage bbsRegionPage) {
        if (null == bbsRegionPage) {
            return Result.error("?????????????????????");
        }
        if (null == bbsRegionPage.getRegionCode() || bbsRegionPage.getRegionCode().isEmpty()) {
            return Result.error("???????????????????????????");
        }
        BbsRegion one = bbsRegionService.lambdaQuery().eq(BbsRegion::getRegionCode, bbsRegionPage.getRegionCode()).one();
        if (null != one) {
            return Result.error("????????????????????????");
        }
        BbsRegion bbsRegion = new BbsRegion();
        BeanUtils.copyProperties(bbsRegionPage, bbsRegion);
        bbsRegionService.addRegion(bbsRegion, bbsRegionPage.getBbsClassList());
        //bbsRegionService.saveMain(bbsRegion, bbsRegionPage.getBbsClassList());
        return Result.OK("???????????????");
    }


    /**
     * ??????????????????
     *
     * @param bbsRegion
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "??????-??????????????????")
    @ApiOperation(value = "??????-??????????????????", notes = "??????-??????????????????")
    @GetMapping(value = "/wise/back/list")
    @PermissionData(pageComponent = "bbs/operator/region/BbsRegionList")
    public Result<?> queryPageListWiseBack(BbsRegion bbsRegion,
                                           @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                           @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                           HttpServletRequest req) {
        QueryWrapper<BbsRegion> queryWrapper = QueryGenerator.initQueryWrapper(bbsRegion, req.getParameterMap());
        Page<BbsRegion> page = new Page<BbsRegion>(pageNo, pageSize);
        IPage<BbsRegion> pageList = bbsRegionService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

}