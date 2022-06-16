package web.comand.impl;

import com.google.gson.Gson;
import dto.validation.IDValidator;
import infrastructure.anotation.Endpoint;
import infrastructure.anotation.InjectByType;
import infrastructure.anotation.NeedConfig;
import infrastructure.anotation.Singleton;
import logiclayer.service.LocalityService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import web.comand.MultipleMethodController;
import web.exception.OnClientSideProblemException;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static web.DispatcherServlet.JSON_RESPONSE;
import static web.constant.AttributeConstants.SESSION_LANG;

/**
 * Process "get/localitiesGet/by/localitySend/id" requests
 *
 * @author Vendelovskyi Ivan
 * @version 1.0
 */
@Singleton
@NeedConfig
@Endpoint("get/localitiesGet/by/localitySend/id")
public class LocalitySendController implements MultipleMethodController {
    private static final Logger log = LogManager.getLogger(LocalitySendController.class);
    private static final String ID = "id";
    @InjectByType
    private LocalityService localityService;
    @InjectByType
    private IDValidator idValidator;

    @Override
    public String doGet(HttpServletRequest request) {
        log.debug("");

        if (!idValidator.isValid(request, ID)) {
            log.error("id is not valid client is broken");
            throw new OnClientSideProblemException();
        }
        return JSON_RESPONSE +
                new Gson().toJson(localityService.getLocaliseLocalitiesGetByLocalitySendId(
                        (Locale) request.getSession().getAttribute(SESSION_LANG),
                        Long.parseLong(request.getParameter(ID))));
    }

    @Override
    public String doPost(HttpServletRequest request) {
        return null;
    }
}
