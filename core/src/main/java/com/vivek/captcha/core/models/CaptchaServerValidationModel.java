package com.vivek.captcha.core.models;

import javax.script.Bindings;

import org.apache.felix.scr.annotations.Reference;
import org.apache.sling.api.SlingHttpServletRequest;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

import com.day.cq.wcm.foundation.forms.FieldDescription;
import com.day.cq.wcm.foundation.forms.FieldHelper;
import com.day.cq.wcm.foundation.forms.ValidationInfo;
import com.vivek.captcha.core.service.CaptchaRequest;
import com.vivek.captcha.core.service.CaptchaService;

import io.sightly.java.api.Use;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CaptchaServerValidationModel implements Use {
    @Override
    public void init(Bindings bindings) {
    	// All standard objects are available as bindings
    	SlingHttpServletRequest request = (SlingHttpServletRequest) bindings.get("request");
    	String captchaResponse = request.getParameter("g-recaptcha-response");
    	boolean isCaptchaValid = getService(CaptchaService.class).isValidCaptcha(CaptchaRequest.builder().captchaResponse(captchaResponse).
    			remoteIpAddress(request.getRemoteHost()).build());
    	if(!isCaptchaValid){
    		FieldDescription desc = FieldHelper.getConstraintFieldDescription(request);
    		ValidationInfo.addConstraintError(request, desc);
    	}
    }
    
    static <T> T getService(Class<T> serviceClass) {
        BundleContext bContext = FrameworkUtil.getBundle(serviceClass).getBundleContext();
        ServiceReference sr = bContext.getServiceReference(serviceClass.getName());
            return serviceClass.cast(bContext.getService(sr));
    }
}
