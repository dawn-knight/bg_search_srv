var basePath = '';
function bindNavigatorMenuEvent() {
	var menus = $('div#navigator-container #navbar li');
	menus.bind('click', function() {
		$('#messageDiv').css('display', 'none');
		setMenu($(this));
	});
}
//category,color,size,attribute,brand,store,siteCategory,tagChain,wordConvert
function initCacheTypeCheckBox() {
	var types = 'category,color,size,attribute,brand,store,siteCategory,tagChain,wordConvert';
//	var typeInfo = ['基础分类', '颜色码和色系', '尺寸码', '商品属性', '品牌', '店铺', '扩展分类', '显示标签', '关键字转换'];
//	var typeArr = types.split(',');
	var typeInfo = ['显示标签 '];
	var typeArr = typeInfo;
	var h = '';
	for(var i = 0, len = typeArr.length; i < len; i ++) {
		var typeName = typeArr[i];
		h += '<input type="checkbox" value="'+typeName+'" id="'+typeName+'" name="cacheType" />';
		h += '<label for="'+typeName+'" style="cursor:pointer;">'+typeInfo[i]+'</label>&nbsp;&nbsp; ';
	}
	
	$('#cacheTypeDiv').html(h);
}
//缓存类型的全选和反选；type:1全选；-1反选
function cacheSeleted(type) {
	type = type || 0;
	if(type == 1) {
		$('input[name="cacheType"]').attr('checked', true);
	} else if (type == -1) {
		$('input[name="cacheType"]').each(function() {
			var obj = $(this);
			var isCheck = obj.attr('checked');
			if(isCheck) {
				obj.removeAttr('checked');
			} else {
				obj.attr('checked', true);
			}
		});
	}
}
function reloadAllCache() {
	var types = $('input[name="cacheType"]:checked').map(function() {
		return $(this).val();
	}).get().join(',');
	
	//reloadCacheTypes
	setFormUrl('reloadCache');
	$('#reloadCacheTypes').val(types);
}

function setMenu(obj) {
	var currentMenu = obj;
	var contents = $('div#page-container > div');
	
	var title = currentMenu.html();
	$('title').html('平台搜索 - ' + title);

	var id = currentMenu.attr('pagename');
	
	currentMenu.siblings().removeClass('navbar-item-selected');
	currentMenu.addClass('navbar-item-selected');
	
	contents.css('display', 'none');
	$('div#'+id).css('display', 'block');
}

var OVER_BLOCK_STYLE = {closeable: false};
var PARAM_STR = null;
var isSubClick = false;
var currentFloatId = null;
function bindA_Event() {
	$('#cancelForm').bind('click', function() {
		$('#formBlock').hidePop();
	});
	
	$('#showParams').bind('click', function() {
		queryAndShowParams();
	});
	
	$('div.subpage-sheet-container, #navigator-container').bind('click', function(){
		if(!isSubClick && currentFloatId) {
			closeFloat(currentFloatId);
		}
		isSubClick = false;
	});
	
	$('#subpage-sheet-1').click(function(){
		isSubClick = true;
	});
}
//查询并显示参数信息
function queryAndShowParams() {
	var paramDiv = $('#paramsDiv');
	if(PARAM_STR == null || PARAM_STR.length < 5) {
		var url = basePath + 'parameter';
		jQuery.ajax({
			url : url,
			type : 'GET',
			success : function(rs) {
				var h = '';
				if(rs && rs.length > 5) {
					var pts = eval(rs);
					for(var i = 0; i < pts.length; i ++) {
						var obj = pts[i];
						h += '<section>';
						h += '	<h3>';
						h += '' + obj.type;
						h += '	</h3>';
						h += '	<div>';
						h += ' ';
						var pms = obj.pms;
						var ht = '';
						for(var j = 0; j < pms.length; j ++) {
							var pm = pms[j];
							var v = pm.value;
							if((v +'').length > 5) {
								v = parseFloat(v.toFixed(2));
							}
							var name = pm.type;
							name = name.substring(name.indexOf('.') + 1);
							ht += '<div style="line-height : 28px;">';
							ht += '<span style="color: gray; display: table-cell;width: 120px; color: black;">' + name + '</span>';
							ht += '<span class="param-span" style="display: table-cell;">'+
							'<input class="hidden" type="hidden" value="'+v+'" />' +
							'<input class="type" type="hidden" value="'+pm.type+'" />' +
							'<input class="param-input" size="5" type="text" onkeyup="this.value=this.value.replace(/[^\\d|\\.]/g,\'\');" onblur="changeValue(this);" style="padding-left: 4px;padding-right: 4px;" value="' + v + '"></span>';
							ht += '<span style="color: gray; padding-left : 20px;display: table-cell;">' + pm.info + '</span>';
							ht += '</div>';
						}
						h += ht + '	</div>';
						h += '</section>';
					}
					PARAM_STR = h;
				} else {
					
				}
				paramDiv.html(h);
			}
		});
	} else {
		paramDiv.html(PARAM_STR);
	}
}
//强制查询并显示参数信息
function queryAndShowParamsRefresh() {
	jQuery('#pw').val('');
	PARAM_STR = null;
	queryAndShowParams();
}

//输入密码后确认
function confirmForm() {
	
	$('#iframeDiv').html('<iframe name="postInfo" id="postInfo" style="border : none; height : 50px; width : 800px; overflow : hidden;" onload="finishPost();"></iframe>');

	$('#imgDiv').fadeIn(0);
	
	$('#postInfo').html('');
	$('#apiForm').submit();
	$('#formBlock').hidePop();
}
function finishPost() {
	if(basePath && basePath.length > 10) {
		$('#imgDiv').fadeOut(0);
	}
}
//回车确认
function confirmToSubmit(event) {
	if(event.keyCode == 13) {
		confirmForm();
	}
}
//点击链接后设置对应的action方法
function setFormUrl(url) {
	var form = $('#apiForm');
	form.attr('action', basePath + url);

	$('#formBlock').showPop(OVER_BLOCK_STYLE);
	
	$('#inputPassword').val('').focus();
}
//设置基础路径
function setBasePath(url) {
	$('span.basePath').html(url);
}

function changeValue(o) {
	var obj = $(o);
	var oldValue = obj.parent('span').find('input.hidden').val();
	var newValue = obj.val();
	if(oldValue != newValue) {
		setBtnBorder();
		obj.css('border', '1px solid red');
		var newValue = parseFloat(newValue);
		obj.val(newValue);
	} else {
		obj.css('border', '1px solid #cccccc');
	}
}

function jsonToString(o){
	if(o.constructor == Array){
		var h = '[';
		for(var i = 0; i < o.length; i += 1){
			var obj = o[i];
			h += jsonToString(obj);
			if(i < o.length - 1){
				h += ',';
			}
		}
		h += ']';
		return h;
	}else{
		return '{'+jts(o)+'}';
	}
}
function jts(o){
	var s = '';
	for(var i in o){
		if(typeof o[i] == 'object'){
			s += i + ':{'+jts(o[i])+'},';
		}else{
			s += i + ':\'' + o[i] + '\',';
		}
	}
	if(s.charAt(s.length - 1) == ','){
		s = s.substring(0, s.length - 1);
	}
	return s;
}
//
function setBtnBorder() {
	var o = $('#needSaveDiv');
	o.stop(true);
	o.animate({border : '2px solid red'}, 500, function(){
		o.animate({border : '2px solid white'}, 500, function(){
			o.animate({border : '2px solid red'}, 500, function(){
				o.animate({border : '2px solid white'}, 500, function(){
				});
			});
		});
	});
}
function saveParams() {
	var pms = [];
	$('span.param-span').each(function(){
		var th = $(this);
		var o = {};
		
		var nv = th.find('input.param-input').val();
		var ov = th.find('input.hidden').val();
		if(nv - 0 == ov - 0) {
			return true;
		}
		
		o.type = th.find('input.type').val();
		o.value = nv;
		o.info = '';
		pms.push(o);
	});
	
	if(pms.length < 1) {
		showMsg('未做修改，无需保存');
		return;
	}
	
	var str = jsonToString(pms);
	
	jQuery.ajax({
		url : './saveparam?pms=' + str + '&password=' + $('#pw').val(),
		type : 'POST',
		success : function(rs) {
			if(rs == null || rs != '1') {
				showMsg('未输入密码或权限不足');
			} else {
				showMsg('保存成功');
				queryAndShowParamsRefresh();
			}
		},
		error : function(r) {
			alert(r);
		}
	});
}
function showMsg(msg) {
	$('#messageDivHtml').html(msg);
	var o = $('#messageDiv');
	o.stop(true).css('left', '440px');
	o.fadeOut(0).css('display', 'block');
	o.stop(true).animate({opacity : 1, left : 600}, 500, function(){
		o.delay(1000).animate({opacity : 0, left : 900}, 500, function(){
			o.fadeOut(0);
		});
	});
}

function showFloat(id, v) {
	currentFloatId = id;
	var con = $('#' + id);
	con.css('left', '150px');
	con.stop(true).fadeIn(100);
	con.animate({left : 0}, 150, function() {
		$('#mongoApiUrlInput').val(v || '');
		if(v) {
			jQuery.ajax({
				url : v,
				type : 'POST',
				success : function(rs) {
					$('#mongoApiRs').html('<pre>' + rs + '</pre>').fadeIn(0);
				},
				error : function(r) {
				}
			});
		}
	});
	
}
function closeFloat(id) {
	var con = $('#' + id);

	$('#mongoApiRs').html('').fadeOut(0);
	con.stop(true).animate({left : 300}, 100, function() {
		con.fadeOut(0);
	});
	currentFloatId = null;
}