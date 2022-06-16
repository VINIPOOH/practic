package web;

import infrastructure.ApplicationContext;
import infrastructure.ApplicationContextImpl;
import infrastructure.factory.ObjectFactory;
import infrastructure.factory.ObjectFactoryImpl;
import infrastructure.currency.CurrencyInfoFromFileLoader;
import infrastructure.сonfig.JavaConfig;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import web.comand.MultipleMethodController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;

import static web.constant.AttributeConstants.CONTEXT;
import static web.constant.AttributeConstants.LOGGED_USER_NAMES;

/**
 * @author Vendelovskyi Ivan
 * @version 1.0
 */
public class DispatcherServlet extends HttpServlet {

    public static final String JSON_RESPONSE = "json-response:";
    private static final Logger log = LogManager.getLogger(DispatcherServlet.class);

    @Override
    public void init() throws ServletException {
        getServletContext().setAttribute(LOGGED_USER_NAMES, new ConcurrentHashMap<String, HttpSession>());

        Map<Class, Object> paramMap = new ConcurrentHashMap<>();
        paramMap.put(ResourceBundle.class, ResourceBundle.getBundle("db-request"));
        ApplicationContext context = new ApplicationContextImpl(new JavaConfig(""), paramMap,
                new ConcurrentHashMap<>(), new CurrencyInfoFromFileLoader());
        ObjectFactory objectFactory = new ObjectFactoryImpl(context);
        context.setFactory(objectFactory);
        context.init();
        getServletContext().setAttribute(CONTEXT, context);
    }

    @Override
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws IOException, ServletException {
        log.debug("servlet called with request - " + request.getRequestURI());

        passOver(request, response, getMultipleMethodCommand(request).doGet(request));
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        log.debug("servlet called with request - " + request.getRequestURI());

        passOver(request, response, getMultipleMethodCommand(request).doPost(request));
    }

    private MultipleMethodController getMultipleMethodCommand(HttpServletRequest request) {
        return ((ApplicationContext) getServletContext().getAttribute(CONTEXT))
                .getCommand(request.getRequestURI().replaceFirst(".*/delivery/", ""));
    }

    private void passOver(HttpServletRequest request, HttpServletResponse response, String page) throws IOException, ServletException {
        if (page.contains("redirect:")) {
            response.sendRedirect(page.replace("redirect:", "/delivery/"));
        } else if (page.startsWith(JSON_RESPONSE)) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();
            out.print(page.replaceFirst(JSON_RESPONSE, ""));
            out.flush();
        } else {
            request.getRequestDispatcher(page).forward(request, response);
        }
    }
}