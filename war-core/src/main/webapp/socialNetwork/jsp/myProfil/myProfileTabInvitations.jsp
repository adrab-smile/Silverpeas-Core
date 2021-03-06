<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
<%@page import="com.stratelia.webactiv.util.ResourceLocator"%>
<%@page import="com.stratelia.silverpeas.util.ResourcesWrapper"%>
<%@page import="com.silverpeas.util.EncodeHelper"%>
<%@page import="com.silverpeas.util.StringUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.stratelia.silverpeas.peasCore.URLManager"%>
<%@page import="com.silverpeas.socialNetwork.invitation.model.InvitationUser"%>
<%@page import="com.silverpeas.socialNetwork.myProfil.servlets.MyProfileRoutes"%>
<%@page import="com.silverpeas.socialNetwork.invitation.servlets.InvitationJSONActions"%>

<%
	List invitations = null;
	String receivedCssClass = "";
	String sentCssClass = "";
	boolean outbox = view.equals(MyProfileRoutes.MySentInvitations.toString());
	if (outbox) {
		invitations = (List) request.getAttribute("Outbox");
		sentCssClass = "class=\"active\"";
	} else {
	  	invitations = (List) request.getAttribute("Inbox");
	  	receivedCssClass = "class=\"active\"";
	}
	ResourcesWrapper resource = (ResourcesWrapper) request.getAttribute("resources");
%>
<style type="text/css">
.inlineMessage {
	<% if (invitations != null && !invitations.isEmpty()) { %>
		display: none;
	<% } %>
} 
</style>
<script type="text/javascript">
var invitationId;
var nbInvitations = <%=invitations.size()%>;
$(function() {
	$( "#dialog-confirmCancel" ).dialog({
		autoOpen: false,
		resizable: false,
		modal: true,
		buttons: {
			"<fmt:message key="GML.yes" />": function() {
				$.getJSON("<%=m_context%>/InvitationJSON",
	                { 
	        			IEFix: new Date().getTime(),
	        			Action: "<%=InvitationJSONActions.IgnoreInvitation%>",
	        			Id: invitationId
	                },
	        		function(data){
	            		if (data.success) {
	            			$("#invitation-"+invitationId).hide('slow');
	            			nbInvitations--;
	            			showEmptyListMessage();
	            		} else {
	                		alert(data.error);
	            		}
	        		});
				$( this ).dialog( "close" );
			},
			"<fmt:message key="GML.no" />": function() {
				$( this ).dialog( "close" );
			}
		}
	});

	$( "#dialog-confirmAccept" ).dialog({
		autoOpen: false,
		resizable: false,
		modal: true,
		buttons: {
			"<fmt:message key="GML.yes" />": function() {
				$.getJSON("<%=m_context%>/InvitationJSON",
	                { 
	        			IEFix: new Date().getTime(),
	        			Action: "<%=InvitationJSONActions.AcceptInvitation%>",
	        			Id: invitationId
	                },
	        		function(data){
	            		if (data.success) {
	            			$("#invitation-"+invitationId).hide('slow');
	            			nbInvitations--;
	            			showEmptyListMessage();
	            			
	            		} else {
	                		alert(data.error);
	            		}
	        		});
				$( this ).dialog( "close" );
			},
			"<fmt:message key="GML.no" />": function() {
				$( this ).dialog( "close" );
			}
		}
	});

	$( "#dialog-confirmIgnore" ).dialog({
		autoOpen: false,
		resizable: false,
		modal: true,
		buttons: {
			"<fmt:message key="GML.no" />": function() {
				$( this ).dialog( "close" );
			},
			"<fmt:message key="GML.yes" />": function() {
				$.getJSON("<%=m_context%>/InvitationJSON",
	                { 
	        			IEFix: new Date().getTime(),
	        			Action: "<%=InvitationJSONActions.IgnoreInvitation%>",
	        			Id: invitationId
	                },
	        		function(data){
	            		if (data.success) {
	            			$("#invitation-"+invitationId).hide('slow');
	            			nbInvitations--;
	            			showEmptyListMessage();
	            		} else {
	                		alert(data.error);
	            		}
	        		});
				$( this ).dialog( "close" );
			}
		}
	});

});

function confirmCancel(id) {
	invitationId = id;
	$( "#dialog-confirmCancel .userName" ).text($("#invitation-"+invitationId+" .txt a.name").text());
	$( "#dialog-confirmCancel" ).dialog("open");
}

function confirmAccept(id) {
	invitationId = id;
	$( "#dialog-confirmAccept .userName" ).text($("#invitation-"+invitationId+" .txt a.name").text());
	$( "#dialog-confirmAccept" ).dialog("open");
}

function confirmIgnore(id) {
	invitationId = id;
	$( "#dialog-confirmIgnore .userName" ).text($("#invitation-"+invitationId+" .txt a.name").text());
	$( "#dialog-confirmIgnore" ).dialog("open");
}

function showEmptyListMessage() {
	if (nbInvitations <= 0) {
		$(".inlineMessage").show('slow');
	}
}
</script>

<div id="invitationProfil">

<div class="sousNavBulle">
	<p><fmt:message key="myProfile.tab.invitations" /> : <a <%=receivedCssClass %> href="<%=MyProfileRoutes.MyInvitations.toString() %>"><fmt:message key="myProfile.invitations.received" /></a> <a <%=sentCssClass %> href="<%=MyProfileRoutes.MySentInvitations.toString() %>"><fmt:message key="myProfile.invitations.sent" /></a></p>
</div>

<div id="ReceiveInvitation">

	<div class="inlineMessage">
		<% if (outbox) { %>
			<fmt:message key="myProfile.invitations.outbox.empty" />
		<% } else { %>
			<fmt:message key="myProfile.invitations.inbox.empty" />
		<% } %>
	</div>
	
	<div id="invitations-list">
		
		<% for (int i=0; i<invitations.size(); i++) {
		  	InvitationUser invitation = (InvitationUser) invitations.get(i);
		  	String senderId = invitation.getUserDetail().getId();
		  	int id = invitation.getInvitation().getId();
		%>
			<div class="a_invitation" id="invitation-<%=id%>">
	                 <div class="profilPhoto"><a href="<%=URLManager.getApplicationURL() %>/Rprofil/jsp/Main?userId=<%=senderId%>"><img class="defaultAvatar" alt="" src="<%=URLManager.getApplicationURL()+invitation.getUserDetail().getAvatar() %>" /></a></div>
	                 <div class="action">
	                 		<% if (outbox) { %>
	                 			<a class="link notification" href="#" onclick="confirmCancel(<%=id %>)"><fmt:message key="myProfile.invitations.cancel" /></a>
	                    	<% } else { %>
								<a onclick="confirmAccept(<%=id %>)" class="link invitation" href="#"><fmt:message key="myProfile.invitations.accept" /></a>
	                    		<a onclick="confirmIgnore(<%=id %>)" class="link notification" href="#"><fmt:message key="myProfile.invitations.ignore" /></a>
	                    	<% } %>
	                    	<a onclick="initNotification(<%=senderId %>,'<%=invitation.getUserDetail().getDisplayedName() %>')" class="link notification" href="#"><fmt:message key="GML.notification.send" /></a>
					</div>
					<div class="txt">
	                	<p>
	                    	<a class="name" href="<%=URLManager.getApplicationURL() %>/Rprofil/jsp/Main?userId=<%=senderId%>"> <%=invitation.getUserDetail().getDisplayedName() %> </a>
	                    </p>
	                    <p>
	                    	<fmt:message key="myProfile.invitations.date" /> <%= resource.getOutputDateAndHour(invitation.getInvitation().getInvitationDate())%>
	                    </p>
	                    <p class="message">
	                    <%=EncodeHelper.javaStringToHtmlParagraphe(invitation.getInvitation().getMessage()) %>
	             		</p>
					</div>
	      </div>
	      <% } %>
	      
	</div>
      
</div>

<div id="dialog-confirmCancel" title="<fmt:message key="myProfile.invitations.dialog.cancel.title" />">
	<p><fmt:message key="myProfile.invitations.dialog.cancel.message" /> <span class="userName"></span> ?</p>
</div>
<div id="dialog-confirmAccept" title="<fmt:message key="myProfile.invitations.dialog.accept.title" />">
	<p><fmt:message key="myProfile.invitations.dialog.accept.message" /> <span class="userName"></span> ?</p>
</div>
<div id="dialog-confirmIgnore" title="<fmt:message key="myProfile.invitations.dialog.ignore.title" />">
	<p><fmt:message key="myProfile.invitations.dialog.ignore.message" /> <span class="userName"></span> ?</p>
</div>
</div>

<%@include file="../notificationDialog.jsp" %>