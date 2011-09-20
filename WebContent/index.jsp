<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
				location.href = location.href.replace(location.host + '/tuqianyi3', 'taocore.w44.mc-test.com/tuqianyi');
			});
		});
	</script>     
</head>

<body oncontextmenu="return false;">
<div class="container">
	<div id="header" class="span-24 last append-bottom">
		<div class="left">
		<img src="images/logo.png"></img>
		<s:if test="%{#session.VERSION == 1}">
	    	<span>初级版</span><span>支持300件宝贝</span>
		</s:if>
		<s:elseif test="%{#session.VERSION == 2}">
    		<span>中级版</span><span>支持800件宝贝</span>
		</s:elseif>
		<s:elseif test="%{#session.VERSION == 3}">
    		<span>高级版</span><span>支持2000件宝贝</span>
		</s:elseif>
		</div>
		<div class="right" style="margin-top: 5px;">
			您好，<span id="nick"><s:property value="#session.USER"/></span><span class="quiet separator">|</span>
			<span id='upgrade'><a href="http://fuwu.taobao.com/using/serv_upgrade.htm?service_id=6371" target="_blank">升级</a></span><span class="quiet separator">|</span>
			<span id='old-edition'><a href="#">返回旧版</a></span><span class="quiet separator">|</span>
			<span><a target='_blank' href='http://amos.im.alisoft.com/msg.aw?v=2&uid=%E8%B5%A4%E7%8F%A0%E5%AD%90&site=cntaobao&s=1&charset=utf-8'><img border='0' src='http://amos.im.alisoft.com/online.aw?v=2&uid=%E8%B5%A4%E7%8F%A0%E5%AD%90&site=cntaobao&s=1&charset=utf-8' alt='联系作者' /></a></span>
		</div>
	</div>
	<div id="tabs" class="span-24 last">
		<ul>
			<li><a href="items_selector.action">图签易</a></li>
			<li><a href="faq.html">常见问题</a></li>
			<li><a href="about.html">服务信息</a></li>
		</ul>
	</div>
	<div id="footer" class="span-24 last prepend-top quiet">
		<a href="http://seller.taobao.com/fuwu/shopshow/shop_index.htm?page_id=95889&isv_id=62112477&page_rank=2&tab_type=1" target="_blank" class="quiet">稻壳科技</a>
	</div>
	<div id="fixed-tools">
		<div id="top"><img src="images/top.png" /></div>
		<div id="bottom"><img src="images/bottom.png" /></div>
	</div>
</div>
</body>
</html>