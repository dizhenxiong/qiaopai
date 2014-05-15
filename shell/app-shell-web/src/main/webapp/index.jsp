<%@ page language="java" pageEncoding="UTF-8"%>
<html>
<body>
	<h2>Hello World!</h2>

	<form action="/player/login" method="post" class="login-box">
		<br> <br>
		<h3>使用您的注册邮箱/葡萄号登录:</h3>
		<label for="name">name</label> <input type="text" id="name"
			name="name" placeholder="用户名" class="login email-input">
		<label for="password">密码</label> <input type="password" id="password"
			name="password" placeholder="Please enter your password here"
			class="login password-input">
	

		<div class="login-action">
			
			<button class="btn btn-primary btn-large pull-right">登录</button>
		</div>
	</form>


</body>
</html>
