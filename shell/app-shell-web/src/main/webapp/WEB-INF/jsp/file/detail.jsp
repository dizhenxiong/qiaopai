<%@ page language="java" import="java.util.*" pageEncoding="utf-8" %>
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
        <form role="form" action="/memory/uploadFile" method="post"   enctype="multipart/form-data" >
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">标题*</label>
                    ${material.title}
                </div>

                <div class="form-group col-md-6">
                    <label for="">材料长度*</label>
                    10米
                </div>
            </div>
            <div class="row">

                <div class="form-group col-md-6">
                    <label for="">备注*</label>

                </div>
                <div class="form-group col-md-6">
                    <label for="">计划时间</label>

                    <div class='input-group date datetimepicker' id=''
                         data-date-format="YYYY/MM/DD">
                        ${material.firstTime}
                        <span class="input-group-addon">
                            <span class="glyphicon glyphicon-calendar"></span>
                        </span>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">邮箱抄送地址</label>
                    ${material.sEmailcc}
                </div>
                <div class="form-group col-md-6">
                    <label for="">截止日期*</label> <label>
                      ${material.deadline}
                </div>
            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">合资公司*</label>
                    ${material.sCompany}
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
                    EMAIL
                </div>
                <div class="form-group col-md-6">
                    <label for="">MediaHub Reference</label>
                </div>

            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">壳牌地区*</label>
                    中国
                </div>
                <div class="form-group col-md-6">
                    <label for=""> <a href="http://download.shell.ptteng.com/${material.url}">附件</a></label>

                </div>
            </div>
            <div class="row">
                <div class="form-group col-md-12">
                    <lable>附言</lable>


                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <a href="/memory/list?id=1">返回</a>
                </div>
            </div>
        </form>
    </div>
    <div class="footer">
        <p>&copy; Company 2014</p>
    </div>

</div>
<script type="text/javascript">
    $(function () {
        $('.datetimepicker').datetimepicker({
            pickTime: false
        });
    });
</script>
</body>
</html>
