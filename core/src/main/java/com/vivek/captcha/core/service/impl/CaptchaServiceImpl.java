package com.vivek.captcha.core.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.osgi.framework.Constants;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Modified;

import com.vivek.captcha.core.JacksonUtils;
import com.vivek.captcha.core.service.CaptchaRequest;
import com.vivek.captcha.core.service.CaptchaService;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;




@Service
@Component(immediate = true, metatype = true)
@Properties({
        @Property(name = Constants.SERVICE_VENDOR, value = "Captcha Service"),
        @Property(name = Constants.SERVICE_DESCRIPTION, value = "Captcha Service")
})
@Slf4j
public class CaptchaServiceImpl implements CaptchaService {
    @Property(label = "Captcha API host",
            description = "Host at which Captcha API sits",
            value = "https://www.google.com")
    private static final String PROPERTY_CAPTCHA_API_HOST = "captcha.validation.api.host";
    
    
    @Property(label = "Captcha Secret Key",
            description = "Captcha Secret Key",
            value = "<Secret Key>")
    private static final String PROPERTY_CAPTCHA_SECRET_KEY = "captcha.validation.secret.key";
    
    @Property(label = "Captcha API path",
            description = "Path at which Captcha API sits",
            value = "/recaptcha/api/siteverify")
    private static final String PROPERTY_CAPTCHA_API_PATH = "captcha.validation.api.path";
  
    
    private String apiPath;
    private String apiHost;
    private String secretKey;
  
    /**
     * Component life cycle method, this is called with each configuration
     * change and with each bundle deployment.
     *
     * @param context
     *            the ComponentContext
     */
    @Activate
    @Modified
    protected final void activate(final ComponentContext context) {
        final Dictionary<?, ?> properties = context.getProperties();
         this.apiHost = org.apache.sling.commons.osgi.PropertiesUtil.toString(properties
                        .get(CaptchaServiceImpl.PROPERTY_CAPTCHA_API_HOST),
                "https://www.google.com");
        this.apiPath = org.apache.sling.commons.osgi.PropertiesUtil.toString(properties
                        .get(CaptchaServiceImpl.PROPERTY_CAPTCHA_API_PATH),
                "/recaptcha/api/siteverify");
        this.secretKey = org.apache.sling.commons.osgi.PropertiesUtil.toString(properties
                .get(CaptchaServiceImpl.PROPERTY_CAPTCHA_SECRET_KEY),
        "<Secret Key>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isValidCaptcha(final CaptchaRequest request) {
    	HttpClient client = new DefaultHttpClient();
    	HttpPost post = new HttpPost(this.apiHost+this.apiPath);
    	boolean isSuccess = false;
    	try {
    		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
    		nameValuePairs.add(new BasicNameValuePair("secret",
    				this.secretKey));
    		nameValuePairs.add(new BasicNameValuePair("response",
    				request.getCaptchaResponse()));
    		nameValuePairs.add(new BasicNameValuePair("remoteip",
    				request.getRemoteIpAddress()));
    		post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

    		HttpResponse response = client.execute(post);
    		BufferedReader rd = new BufferedReader(new InputStreamReader(
    				response.getEntity().getContent()));
    		String line = "";
    		StringBuilder builder = new StringBuilder();
    		while ((line = rd.readLine()) != null) {
    			log.warn("Response from google:" + line);
    			builder.append(line);
    		}
    		GoogleResponse googleResponse = JacksonUtils.getObjectFrom(builder.toString(), GoogleResponse.class);
    		log.warn("Finally from google:" + googleResponse.success);
    		isSuccess = googleResponse.success;
    		
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    	log.warn("Finally from google:" + isSuccess);
    	return isSuccess;
    }

   
    private static class GoogleResponse{
    	@Getter @Setter private boolean success;
    	@Getter @Setter private String challenge_ts;
    	@Getter @Setter  private String hostname;
    	
    }
}
