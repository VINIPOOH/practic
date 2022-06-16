package web.comand.impl;

import dal.entity.User;
import dto.validation.Validator;
import infrastructure.anotation.Endpoint;
import infrastructure.anotation.InjectByType;
import infrastructure.anotation.NeedConfig;
import infrastructure.anotation.Singleton;
import infrastructure.currency.CurrencyInfo;
import logiclayer.exeption.NoSuchUserException;
import logiclayer.exeption.ToMachMoneyException;
import logiclayer.service.UserService;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import web.comand.MultipleMethodController;
import web.exception.OnClientSideProblemException;

import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

import static logiclayer.service.ServicesConstants.RUSSIAN_LANG_COD;
import static web.constant.AttributeConstants.SESSION_LANG;
import static web.constant.AttributeConstants.SESSION_USER;
import static web.constant.PageConstance.*;

/**
 * Process "user/user-profile" page requests
 *
 * @author Vendelovskyi Ivan
 * @version 1.0
 */
@Singleton
@NeedConfig
@Endpoint("user/user-profile")
public class UserProfileController implements MultipleMethodController {
    private static final String MONEY = "money";
    private static final String INPUT_HAS_ERRORS = "inputHasErrors";
    private static final Logger log = LogManager.getLogger(UserProfileController.class);
    @InjectByType
    private UserService userService;

    @Override
    public String doGet(HttpServletRequest request) {
        log.debug("");

        return MAIN_WEB_FOLDER + USER_FOLDER + USER_PROFILE_FILE_NAME;
    }

    @Override
    public String doPost(HttpServletRequest request) {
        boolean isValid = getValidator().isValid(request);
        log.debug("isValidRequest = " + isValid);

        if (!isValid) {
            request.setAttribute(INPUT_HAS_ERRORS, true);
            return MAIN_WEB_FOLDER + USER_FOLDER + USER_PROFILE_FILE_NAME;
        }
        long money = Long.parseLong(request.getParameter(MONEY));
        User user = (User) request.getSession().getAttribute(SESSION_USER);
        final Locale locale = (Locale) request.getSession().getAttribute(SESSION_LANG);
        if (locale.getLanguage().equals(RUSSIAN_LANG_COD)){
            if(money%28!=0){
                request.setAttribute(INPUT_GIRVNAS_NOT_RATE, true);
            }
            money = money/28;

        }
        try {
            userService.replenishAccountBalance(user.getId(), money);
        } catch (NoSuchUserException e) {
            throw new OnClientSideProblemException();
        } catch (ToMachMoneyException e) {
            request.setAttribute(INPUT_HAS_ERRORS, true);
            return MAIN_WEB_FOLDER + USER_FOLDER + USER_PROFILE_FILE_NAME;
        }
        user.setUserMoneyInCents(user.getUserMoneyInCents() + money);
        request.setAttribute(SESSION_USER, user);
        return MAIN_WEB_FOLDER + USER_FOLDER + USER_PROFILE_FILE_NAME;
    }

    private Validator getValidator() {
        return request -> {
            try {
                return Long.parseLong(request.getParameter(MONEY)) > 0;
            } catch (NumberFormatException ex) {
                return false;
            }
        };
    }
}
