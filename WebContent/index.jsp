<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ page import="com.tuqianyi.Constants" %>
<%@ taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>图签易</title>
	<link rel="stylesheet" href="css/redmond/jquery-ui-1.8.15.custom.css" type="text/css" />
	<link rel="stylesheet" href="css/jquery-ui-timepicker-addon.css" type="text/css" />
	<link rel="stylesheet" href="css/Pager.css" type="text/css" />
	<link rel="stylesheet" href="css/jquery.tooltip.css" type="text/css" />
	<link rel="stylesheet" href="css/colorPicker.css" type="text/css" />  
	<link rel="stylesheet" href="css/blueprint/screen.css" type="text/css" media="screen, projection"/> 
    <link rel="stylesheet" href="css/blueprint/print.css" type="text/css" media="print"/>
    <!--[if lt IE 8]><link rel="stylesheet" href="css/blueprint/ie.css" type="text/css" media="screen, projection"><![endif]-->
    
    <link rel="stylesheet" href="css/style.css" type="text/css"/>
    
    <script type="text/javascript" src="js/jquery-1.6.2.min.js"></script>
	<script type="text/javascript" src="js/jquery-ui-1.8.15.custom.min.js"></script>
	<script type="text/javascript" src="js/jquery.tooltip.min.js"></script>
	<!-- script type="text/javascript" src="js/jquery.ui.datepicker-zh-CN.js"></script>
	<script type="text/javascript" src="js/jquery-ui-timepicker-addon.js"></script>
	<script type="text/javascript" src="js/jquery-ui-timepicker-zh-CN.js"></script -->
	<script type="text/javascript" src="js/jquery.pager.js" ></script>
	<script type="text/javascript" src="js/jquery.colorPicker.js" ></script>
	<script type="text/javascript" src="js/jquery.mytable.js" ></script> 

	<script type="text/javascript">
		$(function() {
			$( "#tabs" ).tabs({
				cache: true,
				spinner: '载入中...'
			});
			$("#top").click(function(){
				$(window).scrollTop(0);
			});
			$("#bottom").click(function(){
				$(window).scrollTop($(document).height());
			});
			$("#old-edition").click(function(){
				//location.href = location.href.replace(location.host + '/tuqianyi3/', 'taocore.w44.mc-test.com/tuqianyi/Tuqianyi.html');
				location.href = location.href.replace('/tuqianyi3/', '/tuqianyi/Tuqianyi.html');
			});
		});
	</script>     
</head>

<body <s:if test='!#session.admin'> oncontextmenu="return false;"</s:if>>
<div class="container">
	<div id="header" class="span-24 last append-bottom">
		<div class="left logo">
		<img src="images/logo.png"></img>
		<s:if test="%{#session.VERSION == 1}">
	    	<span>初级版</span><span>支持<%=Constants.ALLOWED_ITEMS_V1%>件宝贝</span>
		</s:if>
		<s:elseif test="%{#session.VERSION == 2}">
    		<span>中级版</span><span>支持<%=Constants.ALLOWED_ITEMS_V2%>件宝贝</span>
		</s:elseif>
		<s:elseif test="%{#session.VERSION == 3}">
    		<span>高级版</span><span>支持<%=Constants.ALLOWED_ITEMS_V3%>件宝贝</span>
		</s:elseif>
		<s:elseif test="%{#session.VERSION == 0}">
    		<span>体验版</span><span>支持<%=Constants.ALLOWED_ITEMS_FREE%>件宝贝</span>
		</s:elseif>
		</div>
		<div class="right" style="margin-top: 5px;">
			<span style='color:red;'>IE6用户请使用旧版</span><span class="quiet separator">|</span>
			您好，<span id="nick"><s:property value="#session.USER"/></span><span class="quiet separator">|</span>
			<span id='upgrade'><a href="http://fuwu.taobao.com/using/serv_upgrade.htm?service_id=6371" target="_blank">升级</a></span><span class="quiet separator">|</span>
			<span id='old-edition'><a href="#">返回旧版</a></span><span class="quiet separator">|</span>
			<span><a target='_blank' href='http://amos.im.alisoft.com/msg.aw?v=2&uid=%E8%B5%A4%E7%8F%A0%E5%AD%90&site=cntaobao&s=1&charset=utf-8'><img border='0' src='http://amos.im.alisoft.com/online.aw?v=2&uid=%E8%B5%A4%E7%8F%A0%E5%AD%90&site=cntaobao&s=1&charset=utf-8' alt='联系作者' /></a></span>
		</div>
	</div>
	<!-- div class="span-23 last notice">
		【公告】服务到期后，已贴标签的宝贝并不会自动恢复。如果到期后不想继续使用此服务，请在到期前自己恢复所有已贴标签的宝贝，否则，将永远无法恢复。
	</div -->
	<div id="tabs" class="span-24 last">
		<ul>
			<s:url action="items_selector" var="itemSelectorLink"></s:url>
			<li><a href="${itemSelectorLink}">图签易</a></li>
			<li><a href="faq.html">常见问题</a></li>
			<s:url action="service_info" var="serviceInfoLink"></s:url>
			<li><a href='${serviceInfoLink}'>服务信息</a></li>
			<s:if test='#session.admin'>
				<s:url action="op-by-categories" var="opByCategoriesLink"></s:url>
				<li><a href="${opByCategoriesLink}">快速操作</a></li>
				<s:url action="admin" var="adminLink"></s:url>
				<li><a href="${adminLink}">Admin</a></li>
			</s:if>
		</ul>
	</div>
	<div id="footer" class="span-24 last prepend-top quiet">
		<a href="http://seller.taobao.com/fuwu/shopshow/shop_index.htm?page_id=95889&isv_id=62112477&page_rank=2&tab_type=1" target="_blank" class="quiet">道和科技</a>
	</div>
	<div id="fixed-tools">
		<div id="top"><img src="images/top.png" /></div>
		<div id="bottom"><img src="images/bottom.png" /></div>
	</div>
</div>
<!-- JiaThis Button BEGIN --> 
<script type="text/javascript"> 
var jiathis_config = { 
	url: "http://fuwu.taobao.com/serv/detail.htm?service_id=6371", 
	title: "#淘宝##卖家#推荐#水印#工具【图签易】，迅速#提升流量#，一分钟上手。", 
	summary:"淘宝 卖家 水印 流量 工具 图签易 一分钟上手" 
};
</script> 
<script src="http://v2.jiathis.com/code/jiathis_r.js?move=0"></script> 
<!-- JiaThis Button END -->
<script type="text/javascript" src="http://toptrace.taobao.com/assets/getAppKey.js" topappkey="12184977"></script>
</body>
</html>