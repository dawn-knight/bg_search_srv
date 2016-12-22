<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ page isELIgnored="false" %> 
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <title>筛选器测试页面</title>
	<meta http-equiv="pragma" content="no-cache" />
	<meta http-equiv="cache-control" content="no-cache" />
	<meta http-equiv="expires" content="0" />    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
	<meta http-equiv="description" content="This is my page" />
	<script type="text/javascript" src="../js/jquery-1.4.2.js"></script>
	
	<script type="text/javascript">
	var url = window.location.href;
	$(function(){
		var index = url.indexOf('MBgoSearchSrv');
		var host = url.substring(0, index);
		url = host + 'MBgoSearchSrv/search/qf';
		showCateFilters(0);
		
		
		$('#curentId').keyup(function(e){

			var kc = e.keyCode;

			if(kc == 13) {
				var thisCid = $(this).val();
				if(thisCid == null || thisCid.length < 1) {
					thisCid = 0;
				}
				showCateFilters(thisCid);
			}
		});
		
		$('#curentId').focus();
	});
	
	function showCurrentCategory(cate) {
		var h = '';
		
		h = dealSubs(cate);
		
		h += '<div style="clear : both;"></div>';
		$('#currentCate').html(h);
	}
	
	function showCateFilters(cid) {
		var nurl = url;
		if(cid > 0) {
			nurl += '?cid=' + cid;
		}
		jQuery.ajax({
			url : nurl,
			type : 'GET',
			success : function(rs) {
				if(rs.length > 20) {
					var resultObject = eval('[' + rs + ']')[0];
					var subFilters = resultObject.subFilters;
					
					showCurrentCategory(resultObject.currentCategory);
					
					var h = '<table>';
					
					for(var i = 0, len = subFilters.length; i < len; i ++) {
						var filter = subFilters[i];
						h += '<tr><td width="200" valign="top" style="text-align:right;"><span class="filterKey">' + filter.name + 
						'<span style="color: white;">（'+filter.code+'）</span></span><div style="clear : both;"></div></td><td valign="top" >';
						
						var values = filter.values;
						for(var j = 0, lenj = values.length; j < lenj; j ++) {
							var value = values[j];
							
							h += '<span class="filterValueName">';
							h += value.name + '<span style="color : black;">（'+value.count+'）</span>' ;
							h += '</span>';
						}
						
						h += '<div style="clear : both;"></div></td></tr>';
					}
					h += '</table>';
					$('#filterDiv').html(h);
				} else {
					$('#filterDiv').html('无...');
				}
			}
		});
	}
	
	function dealSubs(cate) {
		if(cate == null) {
			return;
		}
		
		var currentCid = cate.cateId;
		$('#qfCid').html(currentCid);

		var subs = cate.subs;
		if(subs && subs.length > 0) {
			var h = '<table><tr><td width="200" valign="top" style="text-align:right;"><span class="filterKey">分类 ' +  
			'<span style="color: white;">（category）</span></span><div style="clear : both;"></div></td><td valign="top" >';
			
			for(var i = 0, len = subs.length; i < len; i ++) {
				var thisCate = subs[i];

				h += '<span class="filterValueName">';
				h += thisCate.cateName + '<span style="color: black;">（'+thisCate.count+'）</span>' ;
				h += '</span>';
			}
			h += '<div style="clear : both;"></div></td></tr>';
			h += '</table>';
			return h;
		}
		return '';
	}
 	</script>
	<style type="text/css">
	body {
		font-size : 13px;
	}
	span.filterKey {
		padding : 3px;
		font-weight : bold;
		margin-top : 3px;
		float : right;
		background-color : #7D98B1;
		box-shadow: 1px 1px 1px black ;
	}
	span.filterValueName {
		padding : 3px;
		float : left;
		margin : 3px;
		background-color : #C5D1DC;
		box-shadow: 1px 1px 1px #222222 ;
	}
	</style>
</head>
<body>
	<div style="width : 1200px; margin : auto;">
		<div style="margin-left : 130px;">
			<div>查询分类ID：<input type="text" id="curentId" onkeyup="this.value=this.value.replace(/[^\d]/g,'')" /></div>
			<div>当前分类ID：<span id="qfCid"></span></div>
		</div>
		<div id="currentCate">
			
		</div>
		<div id="filterDiv">
			
		</div>
		<div id="pingDiv">
			
		</div>
	</div>
</body>
</html>
