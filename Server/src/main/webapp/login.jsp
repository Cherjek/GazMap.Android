<!DOCTYPE html>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
   
<%@ taglib prefix='c' uri='http://java.sun.com/jstl/core_rt' %>

<html>
  <head>
    <title>Вход в систему</title>
  </head>

  <body>
  	<center>
	    <c:if test="${not empty param.login_error}">
	      <font color="red">
	        Не верное имя пользователя или пароль.
	      </font>
	    </c:if>
	    <form name="f" action="<c:url value='j_spring_security_check'/>" method="POST">
	      <table>
	        <tr><td>Пользователь:</td><td><input type='text' name='j_username' value='<c:if test="${not empty param.login_error}"><c:out value="${SPRING_SECURITY_LAST_USERNAME}"/></c:if>'/></td></tr>
	        <tr><td>Пароль:</td><td><input type='password' name='j_password'></td></tr>
	        <tr><td><input type="checkbox" name="_spring_security_remember_me"></td><td>Оставаться в системе</td></tr>
	        <tr><td colspan="2"><input name="submit" type="submit" text="Войти" Swidth="100%"/></td></tr>
	      </table>
	    </form>
    </center>
  </body>
</html>