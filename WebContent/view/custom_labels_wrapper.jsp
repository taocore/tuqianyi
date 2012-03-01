<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div class="labels-panel">
	<div>
		<button id='add-label'>添加网络上的标签</button>
		<a id='design' href='http://banner.alimama.com/ml?bannerSize=250x250'
			target='_blank' title='设计完成后请以PNG格式下载并上传到您的图片空间，然后将图片地址添加到此栏。'>设计</a>
	</div>
	<div id="custom-labels">
		<s:include value="custom_labels.jsp" />
	</div>
</div>

<div id="add-label-dialog" title='添加自定义标签'>
	<p>请输入您所要添加的标签图的网络地址，建议使用<span style='font-weight:bold;color:blue;'>PNG</span>格式图片以支持透明并保持最佳质量</p>
	<div title='按 CTRL + V 粘贴'><input id='label-address' type='text' class='text' value='http://'></input></div>
</div>

<script type="text/javascript" src="js/custom_labels_wrapper.js"></script>