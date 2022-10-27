package org.jeecg.modules.bbs.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: 用户点赞记录表
 * @Author: jeecg-boot
 * @Date:   2021-01-30
 * @Version: V1.0
 */
@Data
@TableName("bbs_user_praise")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="bbs_user_praise对象", description="用户点赞记录表")
public class BbsUserPraise implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private java.lang.String id;
	/**创建人*/
    @ApiModelProperty(value = "创建人")
    private java.lang.String createBy;
	/**创建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private java.util.Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private java.lang.String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private java.util.Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private java.lang.String sysOrgCode;
	/**用户账号*/
	//@Excel(name = "用户账号", width = 15)
    //@ApiModelProperty(value = "用户账号")
    //private java.lang.String userId;
	/**贴子ID*/
	@Excel(name = "贴子ID", width = 15)
    @ApiModelProperty(value = "贴子ID")
    private java.lang.String topicId;
	/**留言ID*/
	@Excel(name = "留言ID", width = 15)
    @ApiModelProperty(value = "留言ID")
    private java.lang.String messageId;
	/**评论ID*/
	@Excel(name = "评论ID", width = 15)
    @ApiModelProperty(value = "评论ID")
    private java.lang.String replyId;
}
