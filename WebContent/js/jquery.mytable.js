(function($) {

    $.fn.mytable = function(options) {
        var opts = $.extend({}, $.fn.mytable.defaults, options);
        this.each(function() {
            apply($(this), options);
        });
    };

    function apply(table, options) {
    	$("tbody input.selector", table).change(function(){
    		var selected = $(this).attr("checked");
    		var row = $(this).closest("tr");
    		//alert(row.html());
    		if (selected)
    		{
    			row.addClass("row-selected");
    		}
    		else
    		{
    			row.removeClass("row-selected");
    			$("thead input.selector", table).attr("checked", false);
    		}
    		options.selectionChanged(row);
    	});

    	$("thead input.selector", table).change(function(){
    		if($(this).attr("checked"))
    		{
    			$("tbody input.selector", table).attr("checked", 'checked');
    		}
    		else
    		{
    			$("tbody input.selector", table).removeAttr("checked");
    		}
    		$("tbody input.selector", table).trigger('change');
    	});
    }
    
    $.fn.mytable.defaults = {
			
    };
})(jQuery);
