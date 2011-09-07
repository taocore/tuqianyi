<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>

<s:iterator value="customLabels">
	<div class="label-item custom-label-item" title="单击将标签添加到主图"
		label_id='<s:property value="id"/>'>
		<img src='<s:property value="src"/>' width="100%" height="100%" />
		<div class='custom-label-tool hide'>
			<img src='images/cross_small.png' />
		</div>
	</div>
</s:iterator>

<script type="text/javascript">
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