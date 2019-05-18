<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>

	<head>		
            <meta name="keywords" content="<%= keywords %>">

            <meta name="description" content="onebeartoe.org aims to provide articles and code examples">

            <link rel="stylesheet" type="text/css" href="http://electronics.onebeartoe.org/css/layout.css" />
            <link rel="stylesheet" type="text/css" href="http://electronics.onebeartoe.org/css/style.css" />
            
            <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/layout.css" />
            <link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/style.css" />
            
            <script src="<%= request.getContextPath() %>/controls/scoreboard.js"></script>

            <title><%= title %></title>	
	</head>

	<body>
