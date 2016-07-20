package com.bean;

import java.io.Serializable;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobRelation;

public class Topic extends BmobObject implements Serializable{
	private static final long serialVersionUID = 1L;
	private _User author; // 发帖的用户
	private String content; // 帖子内容
	private BmobFile Contentfigureurl; // 帖子图片
	private Integer lookNumber; //查看数量
	private Integer commentNumber; // 评论数量
	private BmobRelation relation; // 评论列表
	private String type; // 帖子类型
	private boolean isAnonymous; //是否匿名
	public _User getAuthor() {
		return author;
	}
	public void setAuthor(_User author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public BmobFile getContentfigureurl() {
		return Contentfigureurl;
	}
	public void setContentfigureurl(BmobFile contentfigureurl) {
		Contentfigureurl = contentfigureurl;
	}
	public Integer getLookNumber() {
		return lookNumber;
	}
	public void setLookNumber(Integer lookNumber) {
		this.lookNumber = lookNumber;
	}
	public Integer getCommentNumber() {
		return commentNumber;
	}
	public void setCommentNumber(Integer commentNumber) {
		this.commentNumber = commentNumber;
	}
	public BmobRelation getRelation() {
		return relation;
	}
	public void setRelation(BmobRelation relation) {
		this.relation = relation;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isAnonymous() {
		return isAnonymous;
	}
	public void setAnonymous(boolean isAnonymous) {
		this.isAnonymous = isAnonymous;
	}
	
}
