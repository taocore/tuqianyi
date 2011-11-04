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

<script type="text/javascript">

	$("#add-label-dialog").dialog({
		autoOpen: false,
		modal: true,
		width: 350,
		open : function() {
			$('#label-address').focus().select();
		},
		buttons: {
			确定: function() {
				var labelUrl = $('#label-address').val();
				if (!labelUrl)
				{
					alert('图片地址不能为空。');
					return false;
				}
				if (labelUrl.indexOf('http://') != 0)
				{
					alert('错误的网络地址。');
					return false;
				}
				showProcessingDialog();
				var url = "add_label.action";
				var q = "label.src=" + labelUrl;
				$.ajax({
					url: url,
					data: q,
					type: 'POST',
					success: function(data) {
						hideProcessingDialog();
						$("#add-label-dialog").dialog( "close" );
						$("#custom-labels").html(data);
					}
				});
				return false;
			},
			取消: function() {
				$(this).dialog( "close" );
			}
		}
	});

	$("#add-label, #design").button();
	
	$('#add-label').click(function(){
		$('#add-label-dialog').dialog('open');
		return false;
	});
	
	$(".custom-label-item").hover(
		function()
		{
			$(".custom-label-tool", $(this)).show();
		},
		function()
		{
			$(".custom-label-tool", $(this)).hide();
		}
	);
	
	$('.custom-label-tool').click(function(){
		if (confirm('确定删除？'))
		{
			var url = "delete_label.action";
			var q = "label.id=" + $(this).closest('.custom-label-item').attr('label_id');
			var $label = $(this);
			$.ajax({
				url: url,
				data: q,
				type: 'POST',
				success: function(data) {
					if (data == 'ok')
					{
						hideProcessingDialog();
						$label.closest('.custom-label-item').remove();
					}
					else
					{
						alert(data);
					}
				}
			});
		}
		return false;
	});
</script>