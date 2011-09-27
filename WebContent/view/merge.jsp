<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="java.awt.Font" %>
<%@ page import="com.tuqianyi.model.TextLabel" %>
<%@ taglib prefix="s" uri="/struts-tags" %>

<div>
	<form id="label-form">
		<div id="merging-left" class="left">
			<table id="image-table" cellpadding="0" cellspacing="0">
			<tr><td></td><td><img src='images/x.png'/></td></tr>
			<tr>
			<td><img src='images/y.png'/></td>
			<td id='image-td'>
				<div id="main-pic">
				<s:if test="%{item == null}">
						<img src='images/unknown.jpg'/>
				</s:if>
				<s:else>
	    				<img src='<s:property value="item.picUrl"/>_310x310.jpg'/>
				</s:else>
				<div id="frame"></div>
				</div>
			</td>
			</tr>
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
					<div id='text-options-form'>
					<table>
					<tr>
					<td><label>文字：</label></td> 
					<td><input id='text' type='text' class='short-field' title="特殊标记将被替换为真实值，目前支持“#价格#”以及类似“#8.5折#”等格式"/></td>
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
					<tr>
					<td><label>风格：</label></td>
					<td>
					<div id='style'>
						<input id='style1' name='style' type='radio' value='<%=Font.PLAIN%>' checked='checked' /><label for='style1'>正常</label>
						<input id='style2' name='style' type='radio' value='<%=Font.BOLD%>' /><label for='style2'>加粗</label>
						<input id='style3' name='style' type='radio' value='<%=Font.ITALIC%>' /><label for='style3'>斜体</label>
					</div>
					</td>
					</tr>
					<tr>
					<td><label>划线：</label></td>
					<td>
					<div id='line'>
						<input id='line1' name='line' type='radio' value='<%=TextLabel.LINE_NONE%>' checked='checked' /><label for='line1'>无线</label>
						<input id='line2' name='line' type='radio' value='<%=TextLabel.LINE_UNDER%>' /><label for='line2'>下划线</label>
						<input id='line3' name='line' type='radio' value='<%=TextLabel.LINE_THROUGH%>' /><label for='line3'>删除线</label>
						<input id='line4' name='line' type='radio' value='<%=TextLabel.LINE_SLASH%>' /><label for='line4'>斜线</label>
					</div>
					</td>
					</tr>
					<tr>
					<td><label>边框粗细：</label></td>
					<td><div id='border-width'></div></td>
					</tr>
					<tr>
					<td><label>透明度：</label></td>
					<td><div id='text-opacity'></div></td>
					</tr>
					</table>
					</div>
					<div class='button-bar'>
						<button id='merge-changed' class='right'>确定修改</button>
					</div>
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
	var $mainPic = $('#main-pic');
	var $innerImg = $("img", $mainPic);
	if ($mainPic.data('loaded'))
	{
		$mainPic.width($innerImg.width()).height($innerImg.height());
	}

	$innerImg.load(function(){
		$mainPic.width($(this).width()).height($(this).height()).data('loaded', true);
	});
	
	$("#border-width").slider({
		max: 10
	});
	
	$("#opacity,#text-opacity").slider({
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
	
	$('#merge-changed').button().click(function(){
		var $label = $( "#main-pic .ui-selected" );
		var option = $label.data('option');
		option.text = $('#text').val();
		option.color = $('#fore-color').val();
		option.background = $('#back-color').val();
		option.style = $('#style input[name="style"]:checked').val();
		option.line = $('#line input[name="line"]:checked').val();
		option.borderWidth = $('#border-width').slider('value');
		var src = "text.action?label.id=" + option.mid 
				+ "&label.font=" + option.font 
				+ '&label.text=' + encodeURIComponent(option.text)
				+ '&label.color=' + encodeURIComponent(option.color) 
				+ '&label.background=' + encodeURIComponent(option.background)
				+ '&label.style=' + option.style
				+ '&label.line=' + option.line
				+ '&label.borderWidth=' + option.borderWidth
				+ '&t=' + new Date().getTime();
		$label.addClass('loading');
		$("img.t", $label).hide().attr('src', src);
		return false;
	});
	
	$('.color').colorPicker();
	$('#style,#line').buttonset();
	
	function getMerges()
	{
		var $merges = $("#main-pic div.merge");
		var $frame = $("#frame img");
		if (!$merges && !$frame)
		{
			return null;
		}
		var q = '';
		$merges.each(function(i){
			var $this = $(this);
			var m = '&merges[' + i + '].';
			var pos = $this.position();
			q = q + m + 'x=' + pos.left
			 + m + 'y=' + pos.top
			 + m + 'width=' + $this.width()
			 + m + 'height=' + $this.height()
			 + m + 'opacity=' + $this.data('option').opacity;;
			if ($this.hasClass('t'))
			{
				var option = $this.data('option');
				q = q + m + 'textLabel.id=' + $this.data('option').mid
					+ m + "textLabel.font=" + option.font 
					+ m + 'textLabel.text=' + option.text
					+ m + 'textLabel.color=' + option.color 
					+ m + 'textLabel.background=' + option.background
					+ m + 'textLabel.style=' + option.style
					+ m + 'textLabel.line=' + option.line;
			}
			else
			{
				q = q + m + 'imageLabel.src=' + $("img", $this).attr("src");
			}
		});
		var frameSrc = $frame.attr('src');
		if (frameSrc)
		{
			q += '&frame.src=' + frameSrc;
		}
		return q;
	}
</script>