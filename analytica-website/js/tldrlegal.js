$(document).ready(function()
{
    
    $('.info-list li, .toggleList li').click(function()
    {
        if($(this).hasClass('list_header'))
            return;
        if($(this).find('.toggleCollapse').hasClass('active'))
            $(this).find('.toggleCollapse').removeClass('active');
        else
            $(this).find('.toggleCollapse').addClass('active');
        $(this).find('.attr-desc').toggle('fast');
    });
    $('#embed-window').dialog({autoOpen:false,modal:true});
    $('#embed-options').tabs();
    
    $('#embed-btn').click(function() {
            $( "#embed-window" ).dialog("open");
    });

    $('#selectsubmit').change(function(){
        window.location = "/license/"+$(this).val();
    });
});