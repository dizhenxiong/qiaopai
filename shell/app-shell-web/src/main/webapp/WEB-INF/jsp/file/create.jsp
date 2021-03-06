<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core_rt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
    <title>My JSP 'index.jsp' starting page</title>

    <meta http-equiv="pragma" content="no-cache">
    <meta http-equiv="cache-control" content="no-cache">
    <meta http-equiv="expires" content="0">
    <meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
    <meta http-equiv="description" content="This is my page">
    <!-- Bootstrap core CSS -->
    <link href="/r/bootstrap3/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom styles for this template -->
    <link href="/r/style.css" rel="stylesheet">
    <script type="text/javascript" src="/r/jquery/jquery.min.js"></script>
    <script type="text/javascript" src="/r/moment/moment.min.js"></script>
    <script type="text/javascript" src="/r/bootstrap2/js/bootstrap.min.js"></script>
    <script type="text/javascript"
            src="/r/bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js"></script>
    <link rel="stylesheet"
          href="/r/bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.min.css"/>
</head>
<body>
<div class="navbar navbar-inverse">
    <div class="container">
        <div class="navbar-header">
            <a class="navbar-brand hidden-sm" href="/"><img
                    src="/r/img/shell.gif" alt=""/> 壳牌</a>
        </div>
        <div class="navbar-collapse collapse">
            <ul class="nav navbar-nav">
                <li><a type="button" href="/" class="btn">退出</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container">
    <div class="row marketing">
        <form role="form" action="${iurl}" method="post"
              <c:if test="${num==1}">
                  enctype="multipart/form-data"
              </c:if>
              >
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">标题*</label>
                    <input name="title"  class="form-control" id="" placeholder=""  value="${material.title}"  ${adis}>
                </div>

                <div class="form-group col-md-6">
                    <label for="">材料长度*</label>
                    <select class="form-control" ${adis}>
                       <option value="1米">1米</option>
                    </select>
                </div>
            </div>
            <div class="row">

                <div class="form-group col-md-6">
                    <label for="">备注*</label>
                    <select class="form-control" ${adis}>
                      <option value="">Select Material</option>
                    </select>
                </div>
                <div class="form-group col-md-6">
                    <label for="">计划时间</label>

                    <div class='input-group date datetimepicker' id=''   data-date-format="YYYY/MM/DD">
                        <input name="firstTime" type='text' class="form-control" value=" ${material.firstTime}" ${adis}/>
                        <span class="input-group-addon">
                            <span class="glyphicon glyphicon-calendar"></span>
                        </span>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">邮箱抄送地址</label>
                    <input type="email" name="sEmailcc" class="form-control" id="" placeholder=""  value="${material.sEmailcc}" ${adis}>
                </div>
                <div class="form-group col-md-6">
                    <label for="">截止日期*</label> <label>
                    <div class='input-group date datetimepicker' id='' data-date-format="YYYY/MM/DD">
                        <input name="deadline" type='text' class="form-control" value="${material.deadline}" ${adis}/>
                        <span class="input-group-addon">
                            <span class="glyphicon glyphicon-calendar"></span>
                        </span>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">合资公司*</label>
                    <select name="sCompany" class="form-control" ${adis}>
                        <option value="Intel">英特尔</option>
                        <option value="Mico">微软</option>
                    </select>
                </div>
                <div class="form-group col-md-6">
                    <label class="control-label">Job ID</label>

                    <div class="">
                        <p class="form-control-static">Job ID not set yet</p>
                    </div>
                </div>
            </div>
            <div class="row">

                <div class="form-group col-md-6">
                    <label for="">通讯类型*</label>
                    <select class="form-control" ${adis}>
                    <option value="">Select Communication Type</option>
                </select>
                </div>
                <div class="form-group col-md-6">
                    <label for="">MediaHub Reference</label> <input type="email"
                                                                    class="form-control" id="" placeholder="">
                </div>

            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">壳牌地区*</label>
                    <select class="form-control" name="area" ${adis}>
                        <option value="">Select Area of Shell Business or Function</option>
                        <option value="北京">北京</option>
                        <option value="香港">香港</option>
                    </select>
                </div>
                <div class="form-group col-md-6">
                    <label for="exampleInputFile">上传文件</label>
                    <input type="file" name="file" id="exampleInputFile" ${adis}/>
                    <c:if test="${num>1}">
                        <label for=""> <a href="http://download.shell.ptteng.com/${material.url}" target="_blank">附件</a></label>

                    </c:if>

                </div>
            </div>
            <div class="row">
                <div class="form-group col-md-12">
                    <lable>审核意见</lable>
                    <textarea class="form-control" rows="3"  ${bdis}></textarea>

                </div>
            </div>

            <input type="hidden" name="status" value="3"/>
            <input type="hidden" name="mid" value="${material.id}"/>

            <div class="row">
                <div class="col-md-12">
                    <a href="/memory/list?id=${userid}">返回</a>
                    <c:if test="${num == 1}">
                        <button type="submit" class="btn btn-success">提交</button>
                    </c:if>
                    <c:if test="${num == 3}">
                        <%--<button type="submit" class="btn btn-success" id="disagree">拒绝</button>--%>
                        <button type="submit" class="btn btn-success" id="agree">通过</button>
                    </c:if>
                </div>
            </div>
        </form>
    </div>
    <div class="footer">
        <p>&copy; Company 2014</p>
    </div>

</div>

<script type="text/javascript">
    $(function() {
        $('.datetimepicker').datetimepicker({
            pickTime : false
        });
    });    
    
</script>
</body>
</body>
</html>
