package com.minzheng.blog.controller;


import com.minzheng.blog.annotation.OptLog;
import com.minzheng.blog.dto.BlogHomeInfoDTO;
import com.minzheng.blog.entity.UserAbout;
import com.minzheng.blog.service.BlogInfoService;
import com.minzheng.blog.service.impl.WebSocketServiceImpl;
import com.minzheng.blog.vo.Result;
import com.minzheng.blog.constant.StatusConst;
import com.minzheng.blog.vo.VoiceVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.io.IOException;

import static com.minzheng.blog.constant.OptTypeConst.UPDATE;

/**
 * @author xiaojie
 * @since 2020-05-18
 */
@Api(tags = "博客信息模块")
@RestController
public class BlogInfoController {
    @Autowired
    private BlogInfoService blogInfoService;
    @Autowired
    private WebSocketServiceImpl webSocketService;

    @ApiOperation(value = "查看博客信息")
    @GetMapping("/")
    public Result<BlogHomeInfoDTO> getBlogHomeInfo() {
        return new Result<>(true, StatusConst.OK, "查询成功", blogInfoService.getBlogInfo());
    }

    @ApiOperation(value = "查看后台信息")
    @GetMapping("/admin")
    public Result<BlogHomeInfoDTO> getBlogBackInfo() {
        return new Result<>(true, StatusConst.OK, "查询成功", blogInfoService.getBlogBackInfo());
    }

    @ApiOperation(value = "查看关于我信息")
    @GetMapping("/about")
    @ResponseBody
    public Result<String> getAbout() {
        return new Result(true, StatusConst.OK, "查询成功", blogInfoService.getAbout());
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改关于我信息")
    @PutMapping("/admin/about")
    public Result<?> updateAbout(String aboutContent) {
        UserAbout userAbout = UserAbout.builder().id(1).userAbout("about").aboutContent(aboutContent).build();
        blogInfoService.updateAbout(userAbout);
        return new Result<>(true, StatusConst.OK, "修改成功");
    }

    @OptLog(optType = UPDATE)
    @ApiOperation(value = "修改公告")
    @PutMapping("/admin/notice")
    public Result<?> updateNotice(String notice) {
        UserAbout userAbout = UserAbout.builder().id(2).aboutContent(notice).build();
        blogInfoService.updateNotice(userAbout);
        return new Result<>(true, StatusConst.OK, "修改成功");
    }

    @ApiOperation(value = "上传语音")
    @ApiImplicitParam(name = "file", value = "语音文件", required = true, dataType = "MultipartFile")
    @PostMapping("/voice")
    public Result<String> saveVoice(VoiceVO voiceVO) throws IOException {
        webSocketService.sendVoice(voiceVO);
        return new Result<>(true, StatusConst.OK, "上传成功");
    }

    @ApiOperation(value = "查看公告")
    @ResponseBody
    @GetMapping("/admin/notice")
    public Result<String> getNotice() {
        return new Result(true, StatusConst.OK, "查看成功", blogInfoService.getNotice());
    }

}

