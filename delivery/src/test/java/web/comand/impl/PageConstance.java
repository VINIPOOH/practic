package web.comand.impl;

public interface PageConstance {
    String REDIRECT_COMMAND = "redirect:";
    String MAIN_WEB_FOLDER = "/WEB-INF/";
    String USER_FOLDER = "user/";
    String ANONYMOUS_FOLDER = "anonymous/";
    String ADMIN_FOLDER = "adminUsersController/";

    String USERS_JSP = "users.jsp";
    String REGISTRATION_FILE_NAME = "registrationController.jsp";
    String LOGIN_FILE_NAME = "loginController.jsp";
    String INDEX_FILE_NAME = "indexController.jsp";
    String ERROR_404_FILE_NAME = "404.jsp";
    String COUNTER_FILE_NAME = "counterController.jsp";
    String USER_PROFILE_FILE_NAME = "userprofile.jsp";
    String USER_DELIVERY_INITIATION_FILE_NAME = "user-delivery-initiation.jsp";
    String USER_DELIVERY_CONFIRM_DELIVERY_FILE_NAME = "user-delivery-request-confirm.jsp";
    String USER_DELIVERY_GET_CONFIRM_FILE_NAME = "user-deliverys-to-get.jsp";
    String USER_STATISTIC_FILE_NAME = "user-statistic.jsp";

    String BASE_REQUEST_COMMAND = "delivery/";
    String INDEX_REQUEST_COMMAND = "indexController";
    String LOGIN_REQUEST_COMMAND = "loginController";
    String ERROR_404_REQUEST_COMMAND = "404";
    String USER_PROFILE_REQUEST_COMMAND = "user/userprofile";
    String COUNTER_REQUEST_COMMAND = "counterController";
    String USER_DELIVERY_INITIATION_REQUEST_COMMAND = "user-delivery-initiation";

    String REDIRECT_ON_ERROR_404_STRAIGHT = "/delivery/404";


}
