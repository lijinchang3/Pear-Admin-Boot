package com.pearadmin.system.controller;

import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import com.pearadmin.common.constant.ControllerConstant;
import com.pearadmin.common.tools.string.StringUtil;
import com.pearadmin.common.web.base.BaseController;
import com.pearadmin.common.web.domain.request.PageDomain;
import com.pearadmin.common.web.domain.response.Result;
import com.pearadmin.common.web.domain.response.module.ResultTable;
import com.pearadmin.system.domain.SysMail;
import com.pearadmin.system.service.ISysMailService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import java.util.ArrayList;

/**
 * @Author: Heiky
 * @Date: 2021/1/13 15:34
 * @Description:
 */
@RestController
@RequestMapping(ControllerConstant.API_SYSTEM_PREFIX + "mail")
@Api(value = "邮件controller", tags = {"邮件操作接口"})
public class SysMailController extends BaseController {

    /**
     * 基 础 路 径
     */
    private String MODULE_PATH = "system/mail/";

    @Resource
    private ISysMailService sysMailService;

    /**
     * Describe: 邮件管理页面
     * Return: ModelAndView
     */
    @GetMapping("/main")
    public ModelAndView main() {
        return JumpPage(MODULE_PATH + "main");
    }

    /**
     * Describe: 邮件列表数据
     * Param: PageDomain
     * Return: 邮件列表
     */
    @GetMapping("/data")
    public ResultTable data(SysMail sysMail, PageDomain pageDomain) {
        PageInfo<SysMail> page = sysMailService.page(sysMail, pageDomain);
        return pageTable(page.getList(), page.getTotal());
    }

    /**
     * Describe: 邮件发送
     * Return: ModelAndView
     */
    @GetMapping("/add")
    public ModelAndView add() {
        return JumpPage(MODULE_PATH + "add");
    }

    /**
     * Describe: 邮件保存和发送
     * Param: SysMail
     * Return: 操作结果
     */
    @PostMapping("/save")
    public Result save(@RequestBody SysMail sysMail) {
        return decide(sysMailService.save(sysMail));
    }

    /**
     * Describe: 删除邮件
     * Param: String
     * Return: 操作结果
     */
    @DeleteMapping("/remove/{mailId}")
    public Result remove(@PathVariable String mailId) {
        return decide(sysMailService.removeById(mailId));
    }

    /**
     * Describe: 批量删除邮件
     * Param: String
     * Return: 操作结果
     */
    @DeleteMapping("/batchRemove/{ids}")
    public Result batchRemove(@PathVariable String ids) {
        ArrayList<String> idList = CollectionUtil.newArrayList(StringUtil.split(ids, ","));
        return decide(sysMailService.removeByIds(idList));
    }

}
