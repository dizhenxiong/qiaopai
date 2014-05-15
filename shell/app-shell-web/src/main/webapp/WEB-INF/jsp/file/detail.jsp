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
                <li><a href="">退出</a></li>
            </ul>
        </div>
    </div>
</div>

<div class="container">
    <div class="row marketing">
        <form role="form">
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">标题*</label>
                    <input name="title" type="email" class="form-control" id="" placeholder="">
                </div>

                <div class="form-group col-md-6">
                    <label for="">材料长度*</label> <select class="form-control">
                    <option value="">Select</option>
                </select>
                </div>
            </div>
            <div class="row">

                <div class="form-group col-md-6">
                    <label for="">备注*</label> <select class="form-control">
                    <option value="">Select Material</option>
                </select>
                </div>
                <div class="form-group col-md-6">
                    <label for="">计划时间</label>

                    <div class='input-group date datetimepicker' id=''
                         data-date-format="YYYY/MM/DD">
                        <input type='text' class="form-control"/> <span
                            class="input-group-addon"><span
                            class="glyphicon glyphicon-calendar"></span></span>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">邮箱抄送地址</label> <input type="email"
                                                        class="form-control" id="" placeholder="">
                </div>
                <div class="form-group col-md-6">
                    <label for="">截止日期*</label> <label>
                    <div class='input-group date datetimepicker' id=''
                         data-date-format="YYYY/MM/DD">
                        <input type='text' class="form-control"/> <span
                            class="input-group-addon"><span
                            class="glyphicon glyphicon-calendar"></span></span>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="form-group col-md-6">
                    <label for="">合资公司*</label> <select class="form-control">
                    <option value="">Select Area</option>
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
                    <label for="">通讯类型*</label> <select class="form-control">
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
                    <label for="">壳牌地区*</label> <select class="form-control">
                    <option value="">Select Area of Shell Business or
                        Function
                    </option>
                </select>
                </div>


                <div class="form-group col-md-6">
                    <label for="exampleInputFile">上传文件</label> <input type="file"
                                                                      id="exampleInputFile">
                </div>


            </div>
            <div class="row">


                <div class="form-group col-md-12">
                    <lable>附言</lable>
                    <textarea class="form-control" rows="3"></textarea>

                </div>
            </div>

            <div class="row">
                <div class="col-md-12">
                    <button type="button" class="btn btn-success">提交</button>
                    <button type="button" class="btn btn-primary">审核</button>
                </div>
            </div>
        </form>
    </div>

    <div class="footer">
        <p>&copy; Company 2014</p>
    </div>

</div>
<!-- /container -->


<!-- Bootstrap core JavaScript
=======
<form action="/player/offline" method="post" class="login-box">
<div class="login-action">
    <button class="btn btn-primary btn-large pull-right">退出登录</button>
</div>
</form>

<div class="container">
<div class="header">
    <ul class="nav nav-pills pull-right">
        <li class="active"><a href="#">Login</a></li>
        <li><a href="#">List</a></li>
    </ul>
    <h3 class="text-muted">Product Detail Table</h3>
</div>

<div class="row marketing">
    <form  action="/file/uploadFile" method="post"  class="form-signin" role="form" >
        <div class="row">
            <div class="col-md-8">
                <div class="form-group">
                    <label for="">Title*</label>
                    <input name="title" type="email" class="form-control" id="" placeholder="">
                </div>
                <div class="form-group">
                    <label for="">About the Materials*</label>
                    <select class="form-control" name="type">
                        <option value="">Select Material</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="">Email addresses to CC confirmation</label>
                    <input name="emailcc" type="email" class="form-control" id="" placeholder="">
                </div>
                <div class="form-group">
                    <label for="">Country or region of use*</label>
                    <select name="country" class="form-control">
                        <option value="China">China</option>
                        <option value="US">US</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="">Communication Type*</label>
                    <select class="form-control" name="commtyp">
                        <option value="PHONE">PHONE</option>
                        <option value="EMAIL">EMAIL</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="">Area of Shell Business*</label>
                    <select name="area" class="form-control">
                        <option value="CHINA"> CHINA</option>
                        <option value="US"> US</option>
                    </select>
                </div>
                <div class="form-group">
                    <lable>Additional details/notes to Checkers</lable>
                    <textarea class="form-control" rows="3"></textarea>
                </div>
            </div>
            <div class="col-md-4">
                <div class="form-group">
                    <label for="">Length of use of materials*</label>
                    <select name="length" class="form-control">
                        <option value="1HOUR">1HOUR</option>
                        <option value="1DAY">1DAY</option>
                        <option value="1MONTH">1MONTH</option>
                    </select>
                </div>
                <div class="form-group">
                    <label for="">Planned first use</label>

                    <div class='input-group date datetimepicker' id='' data-date-format="YYYY/MM/DD">
                        <input type='text' class="form-control"/>
                        <span name="firstTime" class="input-group-addon">
                           <span class="glyphicon glyphicon-calendar"> </span>
                        </span>
                    </div>
                </div>
                <div class="form-group">
                    <label for="">Key Production Deadline*</label>

                    <div class='input-group date datetimepicker' id='' data-date-format="YYYY/MM/DD">
                        <input name="deadline" type='text' class="form-control"/>
                        <span class="input-group-addon">
                            <span class="glyphicon glyphicon-calendar"></span>
                        </span>
                    </div>
                </div>
                <div class="checkbox">
                    <label> <input type="checkbox" value=""> Urgent priority request(under 24 hours)
                    </label>
                </div>
                <div class="form-group">
                    <label class="control-label">Job ID</label>

                    <div class="">
                        <p class="form-control-static">Job ID not set yet</p>
                    </div>
                </div>
                <div class="form-group">
                    <label for="">File To Upload</label>
                        <input type="file" name="file"  id="" placeholder="/>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <button type="submit" class="btn btn-primary">Submit</button>
            </div>
        </div>
    </form>
</div>

<div class="footer">
    <p>&copy; Company 2014</p>
    </div>

</div>
<!-- /container -->


<!-- Bootstrap core JavaScript
>>>>>>> branch 'master' of https://github.com/dizhenxiong/qiaopai.git
================================================== -->
<!-- Placed at the end of the document so the pages load faster -->

<script type="text/javascript">
    $(function () {
        $('.datetimepicker').datetimepicker({
            pickTime: false
        });
    });
</script>
</body>
</html>
