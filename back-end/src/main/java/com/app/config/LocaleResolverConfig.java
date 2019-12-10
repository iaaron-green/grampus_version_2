package com.app.config;

import com.app.config.Constants;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

@Configuration
public class LocaleResolverConfig extends AcceptHeaderLocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        if (StringUtils.isEmpty(request.getHeader("Accept-Language"))) {
            return Locale.getDefault();
        }

        List<Locale.LanguageRange> list = Locale.LanguageRange.parse(request.getHeader("Accept-Language"));
        return Locale.lookup(list, Constants.SUPPORTED_LOCALES);
    }
}