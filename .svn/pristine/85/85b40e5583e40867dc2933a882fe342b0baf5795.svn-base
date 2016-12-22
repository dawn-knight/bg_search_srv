<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%
  String path = request.getContextPath();
			String basePath = request.getScheme() + "://"
					+ request.getServerName() + ":" + request.getServerPort()
					+ path + "/";
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
<title>接口工具</title>
<meta http-equiv="pragma" content="no-cache" />
<meta http-equiv="cache-control" content="no-cache" />
<meta http-equiv="expires" content="0" />
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3" />
<meta http-equiv="description" content="This is my page" />
<script type="text/javascript" src="../js/jquery-1.4.2.js"></script>
<script type="text/javascript" src="../js/admin.js"></script>
<script type="text/javascript" src="../js/jquery.overblock-1.2.js"></script>
<link rel="stylesheet" type="text/css" href="../js/style.css" />

<script type="text/javascript">
	var host = '';
	$(function() {
		var url = window.location.href;
		var index = url.indexOf('MBgoSearchSrv');
		host = url.substring(0, index);
		basePath = host + 'MBgoSearchSrv/apiTools/';
		setBasePath(url);

		bindNavigatorMenuEvent();
		bindA_Event();
		setMenu($('#recommenderApiPageNav'));

		initCacheTypeCheckBox();
	});

	var pageNo = 0;
	var pageSize = 40;
	function showErrorLog(deltPage) {
		var pid = $('#errorLogPid').val();

		if (deltPage == null) {
			pageNo = 1;
		} else {
			pageNo += deltPage;
		}

		jQuery
				.ajax({
					url : host + 'MBgoSearchSrv/jsp/errorLogs?pid=' + pid
							+ '&pageNo=' + pageNo + '&pageSize=' + pageSize,
					type : 'post',
					success : function(rs) {
						var h = '';
						if (rs && rs.length > 20) {
							var list = eval(rs);
							for (var i = 0, len = list.length; i < len; i++) {
								var obj = list[i];
								h += '<div class="errorLogClassDiv">';
								h += '<span style="padding-right : 8px;">'
										+ obj.addTimeStr + '</span>';
								h += '<span style="color : red; font-weight : bold;">'
										+ (obj.productId ? '<span style=" padding-right : 3px;">['
												+ obj.productId + ']</span>'
												: '') + '</span>';
								h += '<span>' + obj.logMsg + '</span>';
								h += '</div>';
							}
						} else {
							if (pageNo == 1) {
								showMessage('木有结果！！！');
							} else {
								showMessage('已是最后一页！！！');
							}
							if (pageNo > 1) {
								showErrorLog(-1);
							}
						}

						$('#errorLogDiv').html(h);
					}
				});
	}

	function upsertProduct(tp) {
		var pid = $('#productManagerId').val();
		if (pid == null || pid.length < 6) {
			alert('请输入商品ID！');
			return;
		}
		var type = 'update';
		if (tp && tp == -1) {
			if (!confirm("确认删除索引吗？")) {
				return;
			}
			type = 'delete';
		}

		doPost(function() {
			getStringAjax(host
					+ 'MBgoSearchSrv/search/upsertProductById.do?productId='
					+ pid + '&type=' + type, function(rs) {
				if (rs && rs.trim() == 1) {
					if (tp == -1) {
						alert('删除索引成功！');
					} else {
						alert('更新索引成功！');
					}
				} else {
					alert('更新失败！！');
				}
			});
		});
	}

	var isPostSubmit = false;
	function getStart() {
		if (isPostSubmit) {
			alert('不要重复提交！');
			return false;
		} else {
			isPostSubmit = true;
			return true;
		}
	}
	function finished() {
		isPostSubmit = false;
	}
	function doPost(func) {
		if (func && getStart()) {
			func();
		}
	}

	function viewProduct() {
		doPost(function() {
			var pid = $('#productManagerId').val();
			getStringAjax(
					host + 'MBgoSearchSrv/search/qg?productId=' + pid,
					function(rs) {
						if (rs && rs.length > 20) {
							var searchResult = eval('[' + rs + ']')[0];
							if (searchResult.total == 1) {
								var product = searchResult.list[0];
								var colorProducts = product.colorProducts;
								var h = '';

								h += '<table class="infoTable">';
								h += '<tr class="infoTableTr"><td>颜色码</td><td>颜色</td><td>库存值</td></tr>';
								for (var i = 0, len = colorProducts.length; i < len; i++) {
									var cp = colorProducts[i];
									var colorCodeId = cp.colorCodeId;
									h += '<tr class="infoTableValueTr '
											+ colorCodeId
											+ '" onclick="setTr(\''
											+ colorCodeId + '\');">';
									h += '<td>' + colorCodeId + '</td>';
									h += '<td>' + cp.colorName + '</td>';
									h += '<td>' + cp.stock + '</td>';
									h += '';
									h += '</tr>';
								}
								h += '</table>';

								h += '<hr class="hrClass1" />';
								h += '<div>';
								h += conbainInfo('标题', product.productName);
								h += conbainInfo('品牌', product.brandName + '（'
										+ product.brandCode + '）');
								h += conbainInfo('市场价', product.marketPrice);
								h += conbainInfo('展示标签', product.displayTag);
								h += conbainInfo('主图', product.imgUrl);
								h += conbainInfo('店铺ID', product.storeId);
								h += conbainInfo('库存', product.stock);
								h += '</div>';

								$('#indexInfoDiv').html(h);
								setTr();
							} else {
								$('#indexInfoDiv').html('无结果！');
							}
							;
						} else {
							$('#indexInfoDiv').html('无结果！');
						}
						;
					});
		});
	}

	function conbainInfo(k, v) {
		if (v && (v + '').length > 0) {
			return '<div><span style="font-weight : bold; width : 80px; display: inline-block;">'
					+ k + '： </span>' + v + '</div>';
		}
		return '';
	}

	function viewStockFromStockCenter() {
		doPost(function() {
			var pid = $('#productManagerId').val();
			getStringAjax(
					host + 'MBgoSearchSrv/jsp/stock?pid=' + pid,
					function(rs) {
						if (rs && rs.length > 20) {
							var stockObjList = eval(rs);
							var len = stockObjList.length;
							var array = [];
							for (var i = 0; i < len; i++) {
								var stockObj = stockObjList[i];
								var colorCodeId = stockObj.colorCodeId.trim();
								obj = array[colorCodeId];
								if (obj) {
									obj.stock += (stockObj.stock - 0);
								} else {
									array[colorCodeId] = {
										colorCodeId : colorCodeId,
										stock : (stockObj.stock - 0)
									};
								}
							}
							var h = '';
							h += '<table class="infoTable">';
							h += '<tr class="infoTableTr"><td>颜色码</td><td>库存值</td></tr>';
							for ( var i in array) {
								var stockObj = array[i];
								var colorCodeId = stockObj.colorCodeId;
								h += '<tr class="infoTableValueTr '
										+ colorCodeId + '" onclick="setTr(\''
										+ colorCodeId + '\');">';
								h += '<td>' + colorCodeId + '</td>';
								h += '<td colspan="2">' + stockObj.stock
										+ '</td>';
								h += '';
								h += '</tr>';
							}
							h += '</table>';

							$('#stockCenterInfoDiv').html(h);
							setTr();
						} else {
							$('#stockCenterInfoDiv').html('无结果！');
						}
					});
		});
	}

	function getStringAjax(url, func) {
		jQuery.ajax({
			url : url,
			type : 'post',
			success : function(rs) {
				func(rs);
				finished();
			}
		});
	}

	function showMessage(msg) {
		var obj = $('#errorMsgLogMsg');
		obj.html(msg);
		obj.css('opacity', 1);
		obj.stop(true).fadeIn(1, function() {
			obj.fadeOut(1800);
		});
	}

	var currentColorCodeId = null;
	function setTr(cid) {
		cid = cid || currentColorCodeId;
		if (cid) {
			$('tr.infoTableValueTr').css('background-color', 'white');
			$('tr.' + cid).css('background-color', '#B8C6D4');
			currentColorCodeId = cid;
		}
	}

	function tagged() {
		document.getElementById('light').style.display = 'block';
		document.getElementById('fade').style.display = 'block';
	}

	function closeDiv() {
		document.getElementById('light').style.display = 'none';
		document.getElementById('fade').style.display = 'none';
	}

	function send() {
		jQuery.ajax({
			url : "/MBgoSearchSrv/apiTools/rebuildAndUpdateSwitch",
			type : 'post',
			data : $("#open").serialize(),
			dataType : "text",
			success : function(rs) {
				$("#iframeDiv").html(rs);
				$("#password").val("");
				closeDiv();
			},
			error : function(a, b, c) {
				alert(a + "" + b + "" + c);
			}
		});
	}
</script>
<style type="text/css">
div.errorLogClassDiv {
	font-size: 13px;
	color: gray;
}

#productInfoIndexDisplay table {
	border-collapse: collapse;
}

#productInfoIndexDisplay td {
	border: 1px solid gray;
	padding: 5px;
}

div[id*="InfoDiv"] {
	width: 535px;
	word-break: break-all;
	word-wrap: break-word;
	padding: 8px;
	font-size: 13px;
}

table.infoTable td {
	padding: 5px;
}

tr.infoTableTr {
	text-align: center;
	font-weight: bold;
}

tr.infoTableValueTr {
	cursor: pointer;
}

hr.hrClass1 {
	border: none;
	border-bottom: 1px solid #D9E0E7;
}

.white_content {
	display: none;
	position: absolute;
	top: 25%;
	left: 25%;
	width: 20%;
	height: 30%;
	padding: 20px;
	border: 5px solid #94B2F4;
	background-color: white;
	z-index: 1002;
	overflow: auto;
}

.pointer {
	cursor: pointer;
}

.right {
	float: right;
	margin-top: -20px;
	margin-right: -20px;
}
</style>
</head>
<body>

	<div id="navigator-container">
		<h1 id="navbar-content-title">选项</h1>
		<div style="height: 70px; width: 1px;"></div>
		<ul id="navbar">
			<!-- <li id="parameterSetPageNav" class="navbar-item navbar-item-selected" pagename="parameterSet">参数设置</li> -->

			<li id="recommenderApiPageNav" class="navbar-item"
				pagename="recommenderApi">搜索接口</li>
			<li id="dataDebugPageNav" class="navbar-item" pagename="dataDebug">数据调试</li>
			<!-- 			<li id="searchErrorLogNav" class="navbar-item" pagename="searchErrorLog">错误日志</li> -->
			<li id="productInfoIndexNav" class="navbar-item"
				pagename="productInfoIndex">单品信息索引</li>
		</ul>
	</div>
	<div id="mainview">
		<div id="navigarot-content">
			<div id="page-container">
				<div id="parameterSet" hidden>
					<h1 class="content-title">参数设置</h1>
					<div class="displaytable">
						<section>
						<h3>参数类型</h3>
						<div>
							<input type="button" id="showParams" value="查看参数" /> <input
								type="button" id="refreshParams"
								onclick="queryAndShowParamsRefresh();" value="刷新查看" />
						</div>
						</section>
						<div id="paramsDiv"></div>
						<div style="padding-top: 20px;">
							<div
								style="margin-bottom: -42px; margin-top: 10px; margin-left: 60px; font-size: 16px; color: #dddddd;">请输入管理员密码</div>
							<span
								style="display: inline-block; float: left; margin-left: 200px; margin-bottom: -40px;">
								<a style="color: gray; text-decoration: none;"
								href="javascript:void(0);" onclick="jQuery('#pw').val('');">✘清空</a>
							</span> <input type="password"
								style="margin-left: 12px; background-color: transparent; padding-left: 5px; width: 180px;"
								id="pw" />
							<div id="needSaveDiv" style="border: 2px solid white;">
								<input type="button" id="saveParams" onclick="saveParams();"
									value=" 保 存 修 改" />
							</div>
						</div>
					</div>
				</div>
				<div id="recommenderApi" hidden>
					<h1 class="content-title">搜索接口</h1>
					<div class="displaytable">
						<!-- 					
						<div id="recoApiDiv"><br /><br />
							<a target="_blank" href="http://10.100.20.245/ApiManager/api/interface?pid=1">http://10.100.20.245/ApiManager/api/interface?pid=1</a>
						</div>
 -->
						<h3>If you have seen these words, congratulations! So many
							aspects waiting to improve, keep hard working!</h3>
					</div>
				</div>

				<div id="dataDebug" hidden style="width: 1000px;">
					<h1 class="content-title">数据调试</h1>
					<div class="displaytable">
						<section>
						<h3>索引信息管理</h3>
						<div>
							<div class="radio">
								<a href="javascript:void(0);"
									onclick="setFormUrl('rebuildProduct');" href="#" class="formA">重建商品基础索引信息</a>
							</div>
							<div class="radio">
								<a href="javascript:void(0);"
									onclick="setFormUrl('buildAutokey');" href="#" class="formA">重建关键字自动补全索引</a>
							</div>
							<div class="radio">
								<a href="javascript:void(0);"
									onclick="setFormUrl('buildKeywordDic');" href="#" class="formA">重建行业词库索引</a>
							</div>

							<div class="radio">
								<a href="javascript:void(0);" onclick="tagged();" href="#"
									class="formA">重建和更新索引开关</a>
							</div>
							<!-- 							        
							        <div class="radio">
							        	<a href="javascript:void(0);" onclick="setFormUrl('buildSpellCheck');" href="#" class="formA">重建拼写检查索引</a>
							        </div>
 -->
						</div>
						</section>

						<section>
						<h3>缓存管理</h3>
						<div>
							<div class="radio" id="cacheTypeDiv"></div>
							<div>：）(This checkbox will also disappear in later
								version.)</div>
							<div class="radio">
								<!-- 										<a href="javascript:void(0);" onclick="cacheSeleted(1);" href="#" class="formA">全选</a>&nbsp;| -->
								<!-- 										<a href="javascript:void(0);" onclick="cacheSeleted(-1);" href="#" class="formA">反选</a>&nbsp;| -->
								<a href="javascript:void(0);" onclick="reloadAllCache();"
									href="#" class="formA">重新加载选择的缓存</a>
							</div>
						</div>
						</section>
						<br />
						<div>
							<div id="imgDiv" style="display: none;">
								<img src="../js/38.gif" />
							</div>
							<div id="iframeDiv" style="font-size: 13px;"></div>
						</div>
					</div>
				</div>
				<!-- 				
				<div id="searchErrorLog" hidden style="width : 1200px;">
					<h1 class="content-title">重要日志</h1>
					
					<div class="displaytable">

						<section>
							<table>
								<tr>
									<td>商品ID：</td>
									<td><input type="text" id="errorLogPid" /></td>
									<td><input type="button" value="查看" onclick="showErrorLog();" /></td>
									<td><input type="button" value="上一页" onclick="showErrorLog(-1);" /></td>
									<td><input type="button" value="下一页" onclick="showErrorLog(1);" /></td>
									<td>
									<span id="errorMsgLogMsg" style="padding-left : 20px; color : red; font-weight : bold; font-size : 18px;"></span>
									</td>
								</tr>
							</table>
						</section>
						<div style="padding-top : 4px;">
							<div id="errorLogDiv">
								
							</div>
						</div>
					</div>
				</div>
-->

				<div id="productInfoIndex" hidden style="width: 1200px;">
					<h1 class="content-title">商品管理</h1>

					<div class="displaytable" id="100022591">
						<section>
						<table>
							<tr>
								<td>商品ID：</td>
								<td><input type="text" value="" id="productManagerId" /></td>
								<td><input type="button" value="更新索引"
									onclick="upsertProduct();" /></td>
								<td><input type="button" value="删除索引"
									onclick="upsertProduct(-1);" /></td>
							</tr>
						</table>
						</section>
						<div style="padding-top: 4px;">
							<div id="productInfoIndexDisplay">
								<table style="border: 1px solid red;">
									<tr>
										<td width="550" style="text-align: center;">索引库信息<input
											type="button" value="查看" onclick="viewProduct();" /></td>
										<!-- 										<td width="550" style="text-align:center;">库存中心信息<input type="button" value="查看" onclick="viewStockFromStockCenter();" /></td> -->
									</tr>
									<tr>
										<td height="260" valign="top"><div id="indexInfoDiv"></div></td>
										<!-- 										<td height="260" valign="top"><div id="stockCenterInfoDiv"></div></td> -->
									</tr>
								</table>
							</div>
						</div>
					</div>
				</div>

			</div>
		</div>
	</div>

	<div id="formBlock">
		<form id="apiForm" action="" method='post' target="postInfo">
			<div
				style="padding: 10px; border-bottom: 1px solid rgba(188, 193, 208, .5); font-weight: bold; color: gray;">
				请输入管理员密码</div>
			<div id="formDiv">
				<div style="padding-top: 50px; padding-left: 88px;">
					<input type="password" id="inputPassword"
						onkeydown="confirmToSubmit(event);"
						style="border: 1px solid #aaaaaa;" name="password" />
				</div>
			</div>
			<input type="hidden" name="type" id="reloadCacheTypes" value="" />
			<div
				style="text-align: right; padding-top: 10px; padding-right: 10px; border-top: 1px solid rgba(188, 193, 208, .5);">
				<input type="button" id="submitForm" onclick="confirmForm();"
					value="确认执行" /> <input type="button" id="cancelForm" value="取消" />
			</div>
		</form>
	</div>

	<div id="messageDiv">
		<div id="messageDivHtml">测试信息</div>
	</div>
	<!-- 开启或关闭自动重建 -->
	<div id="light" class="white_content">
		<!-- 	<a href = "javascript:void(0)" onclick = "document.getElementById('light').style.display='none';document.getElementById('fade').style.display='none'">点这里关闭本窗口</a> -->

		<a class="right pointer" onclick="closeDiv();">X</a>


		<form id="open" name="open" action="" method="post">

			<div
				style="padding: 10px; border-bottom: 1px solid rgba(188, 193, 208, .5); font-weight: bold; color: gray;">
				请输入管理员密码:</div>
			<div id="openDiv">
				<div style="padding-top: 50px; padding-left: 88px;">
					<input type="password" id="password" name="password"
						style="border: 1px solid #aaaaaa;" />
				</div>
				<div style="padding-top: 50px; padding-left: 88px;">
					<input type="radio" name="openFlag" checked="checked" id="open"
						value="1" /> 开启 <input type="radio" name="openFlag" id="close"
						value="0" /> 关闭
				</div>
				<div style="padding-top: 50px; padding-left: 88px;">
					<input type="button" id="tj" name="tj" value="确认执行"
						onclick="send();" /> <input type="button" id="cancle"
						name="cancle" value="取消" onclick="closeDiv();" />
				</div>
			</div>
		</form>

	</div>



</body>
</html>
