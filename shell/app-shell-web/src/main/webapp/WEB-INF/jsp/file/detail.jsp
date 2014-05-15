<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>


<title>My JSP 'index.jsp' starting page</title>

<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
<meta http-equiv="description" content="This is my page">
<!--
    <link rel="stylesheet" type="text/css" href="styles.css">
    -->

 
    <!-- Bootstrap core CSS -->
    <link href="/r/bootstrap3/css/bootstrap.min.css" rel="stylesheet">


    <!-- Custom styles for this template -->
    <link href="style.css" rel="stylesheet">



<script type="text/javascript" src="/r/jquery/jquery.min.js"></script>
<script type="text/javascript" src="/r/moment/moment.min.js"></script>
<script type="text/javascript" src="/r/bootstrap2/js/bootstrap.min.js"></script>
<script type="text/javascript"
	src="/r/bootstrap-datetimepicker/build/js/bootstrap-datetimepicker.min.js"></script>
<link rel="stylesheet"
	href="/r/bootstrap-datetimepicker/build/css/bootstrap-datetimepicker.min.css" />

</head>

<body>
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
			<form role="form">
				<div class="row">
					<div class="col-md-8">

						<div class="form-group">
							<label for="">Title*</label> <input type="email"
								class="form-control" id="" placeholder="">
						</div>
						<div class="form-group">
							<label for="">About the Materials*</label> <select
								class="form-control">
								<option value="">Select Material</option>
							</select>
						</div>
						<div class="form-group">
							<label for="">Email addresses to CC confirmation</label> <input
								type="email" class="form-control" id="" placeholder="">
						</div>
						<div class="form-group">
							<label for="">Country or region of use*</label> <select
								class="form-control">
								<option value="">Select Area</option>
							</select>
						</div>
						<div class="form-group">
							<label for="">Communication Type*</label> <select
								class="form-control">
								<option value="">Select Communication Type</option>
							</select>
						</div>
						<div class="form-group">
							<label for="">Area of Shell Business*</label> <select
								class="form-control">
								<option value="">Select Area of Shell Business or
									Function</option>
							</select>
						</div>
						<div class="form-group">
							<lable>Additional details/notes to Checkers</lable>
							<textarea class="form-control" rows="3"></textarea>
						</div>
					</div>
					<div class="col-md-4">
						<div class="form-group">
							<label for="">Length of use of materials*</label> <select
								class="form-control">
								<option value="">Select</option>
							</select>
						</div>
						<div class="form-group">
							<label for="">Planned first use</label>
							<div class='input-group date datetimepicker' id=''
								data-date-format="YYYY/MM/DD">
								<input type='text' class="form-control" /> <span
									class="input-group-addon"><span
									class="glyphicon glyphicon-calendar"></span></span>
							</div>
						</div>
						<div class="form-group">
							<label for="">Key Production Deadline*</label>
							<div class='input-group date datetimepicker' id=''
								data-date-format="YYYY/MM/DD">
								<input type='text' class="form-control" /> <span
									class="input-group-addon"><span
									class="glyphicon glyphicon-calendar"></span></span>
							</div>
						</div>
						<div class="checkbox">
							<label> <input type="checkbox" value=""> Urgent
								priority request(under 24 hours)
							</label>
						</div>
						<div class="form-group">
							<label class="control-label">Job ID</label>
							<div class="">
								<p class="form-control-static">Job ID not set yet</p>
							</div>
						</div>
						<div class="form-group">
							<label for="">MediaHub Reference</label> <input type="email"
								class="form-control" id="" placeholder="">
						</div>
					</div>
				</div>
				<div class="row">
					<div class="col-md-12">
						<button type="button" class="btn btn-primary">Primary</button>
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
================================================== -->
	<!-- Placed at the end of the document so the pages load faster -->

	<script type="text/javascript">
		$(function() {
			$('.datetimepicker').datetimepicker({
				pickTime : false
			});
		});
	</script>
</body>
</html>
