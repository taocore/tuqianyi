<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div>
	<form id="label-form">
		<div id="merging-left" class="left">
			<table id="image-wrapper" cellpadding="0" cellspacing="0">
			<tr><td>
				<div id="main-pic">
				<s:if test="%{item == null}">
						<img src='images/unknown.jpg'/>
				</s:if>
				<s:else>
	    				<img src='<s:property value="item.picUrl"/>_310x310.jpg'/>
				</s:else>
				<div id="frame"></div>
				</div>
			</td></tr>
			</table>
			<div id='options'>
				<fieldset id="image-label-options" class='hide'>
					<legend>标签选项</legend>
					<!-- label>缩放时保持长宽比：</label> 
					<input id='keep-ratio' type='checkbox' checked='checked' /><br /-->
					<label>透明度：</label>
					<div id="opacity"></div>
				</fieldset>
				
				<fieldset id="text-label-options" class='hide'>
					<legend>标签选项</legend>
					<table>
					<tr>
					<td><label>文字：</label></td> 
					<td><input type='text' class='short-field'/></td>
					</tr>
					<tr>
					<td><label>文字颜色：</label></td>
					<td>
					<input id='fore-color' type='text' class='color' value='#ff0000'/>
					</td>
					</tr>
					<tr>
					<td><label>背景颜色：</label></td>
					<td>
					<input id='back-color' type='text' class='color'/>
					</td>
					</tr>
					</table>
				</fieldset>
			</div>
		</div>
		<div id="ctabs" class="right">
				<ul>
					<li><a href="labels.action">标签</a></li>
					<li><a href="view/text.jsp">文字</a></li>
					<li><a href="frames.action">边框</a></li>
				</ul>
		</div>
	</form>
</div>

<script type="text/javascript">
	$("#opacity").slider({
		value: 100,
		change: function(event, ui) {
			var value = $(this).slider('value');
			var $label = $( "#main-pic .ui-selected" );
			$label.data('option').opacity = value;
			$label.css({
				'opacity': value/100,
				'filter': 'alpha(opacity = ' + value + ')'
			});
		}
	});
	
	$('.color').colorPicker();
	
	function getMerges()
	{
		var q = '';
		$("#main-pic div.merge").each(function(i){
			var m = '&merges[' + i + '].';
			var pos = $(this).position();
			q = q + m + 'x=' + pos.left
			 + m + 'y=' + pos.top
			 + m + 'width=' + $(this).width()
			 + m + 'height=' + $(this).height()
			 + m + 'label.src=' + $("img", $(this)).attr("src")
			 + m + 'label.opacity=' + $(this).data('option').opacity;
		});
		var frameSrc = $("#frame img").attr('src');
		if (frameSrc)
		{
			q += '&frame.src=' + frameSrc;
		}
		return q;
	}
</script>