<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<div id="labels">
	<s:iterator value="labelCategories">
		<h3><a href="#"><s:property value="name" /></a></h3>
		<div class="labels-panel">
			<s:iterator value="labels">
				<div class="label-item" title="单击将标签添加到主图">
					<img src='<s:property value="src"/>' width="100%" height="100%" />
				</div>
			</s:iterator>
		</div>
	</s:iterator>
	<h3><a href="#">自定义标签</a></h3>
	<div class="labels-panel">
		<div>
			<button id='add-label'>添加网络上的标签</button>
			<a id='design' href='http://banner.alimama.com/ml?bannerSize=250x250' target='_blank' title='设计完成后请以PNG格式下载并上传到您的图片空间，然后将图片地址添加到此栏。'>设计</a>
		</div>
		<div>
			<s:iterator value="customLabels">
				<div class="label-item custom-label-item" title="单击将标签添加到主图" label_id='<s:property value="id"/>'>
					<img src='<s:property value="src"/>' width="100%" height="100%" />
					<div class='custom-label-tool hide'><img src='images/cross_small.png'/></div>
				</div>
			</s:iterator>
		</div>
	</div>
</div>

<div id="add-label-dialog" title='添加自定义标签'>
	<p>请输入您所要添加的标签图的网络地址，建议使用PNG图片以支持透明并保持最佳质量</p>
	<input id='label-address' type='text' class='text' value='http://'></input>
</div>

<script type="text/javascript">
	$("#labels").accordion();
	
	$(".label-item").click(
		function() {
			var $label = $("img", this);
			var src = $label.attr('src');
			$label.removeAttr('width').removeAttr('height');
			var w = $label.width();
			var h = $label.height();
			$label.attr('width', '100%').attr('height', '100%');
			var width;
			var height;
			if (w >= h)
			{
				width = 100;
				height = h * 100 / w;
			}
			else
			{
				height = 100;
				width = w * 100 / h;
			}
			var $labelItem = $("<div class='merge'><img src='" + src + "' width='100%' height='100%'/></div>");
			$labelItem.appendTo("#main-pic")
			.css({
				width: width,
				height: height,
				border: '1px solid lightblue',
				position: 'absolute',
				top: 0,
				left: 0,
				cursor: 'move'
			}).draggable({
				containment : "#main-pic",
				scroll : false
			}).resizable({
				containment : "#main-pic",
				aspectRatio : true,
				minWidth : 20,
				minHeight : 20,
				maxWidth : 310,
				maxHeight : 310
			}).hover(
				function()
				{
					$(".label-tool", $labelItem).show();
				},
				function()
				{
					$(".label-tool", $labelItem).hide();
				}
			).dblclick(function(){
				var pos = $(this).position();
				alert(pos.left + ", " + pos.top);
			});
			
			$("<div class='label-tool hide'><img src='images/cross_small.png'/></div>")
			.appendTo($labelItem)
			.css({
				position: 'absolute',
				top: '0',
				right: '0',
				display: 'none',
				cursor: 'pointer'
			}).click(function(){
				$labelItem.remove();
			});
		});
	
	$("#add-label, #design").button();
	
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
						if (data == 'ok')
						{
							hideProcessingDialog();
							$("#add-label-dialog").dialog( "close" );
							//reload(clearSelection);
						}
						else
						{
							alert(data);
						}
					}
				});
				return false;
			},
			取消: function() {
				$(this).dialog( "close" );
			}
		}
	});
	
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
			$.ajax({
				url: url,
				data: q,
				type: 'POST',
				success: function(data) {
					if (data == 'ok')
					{
						hideProcessingDialog();
						$('.custom-label-tool').closest('custom-label-item').remove();
						//reload(clearSelection);
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