package com.ouag.pocs.springbootadminclient;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.info.ConditionalOnEnabledInfoContributor;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.info.InfoContributorProperties;
import org.springframework.boot.actuate.info.GitInfoContributor;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.info.ProjectInfoAutoConfiguration;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Properties;

@SpringBootApplication
public class SpringBootAdminClientApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootAdminClientApplication.class, args);
	}


	@Bean
	@ConditionalOnMissingBean
	@Order(InfoContributorAutoConfiguration.DEFAULT_ORDER)
	public GitCoreInfoContributor gitCoreInfoContributor(GitProperties gitProperties,
												 InfoContributorProperties infoContributorProperties) {
		return new GitCoreInfoContributor(gitProperties, infoContributorProperties.getGit().getMode());
	}



	@ConditionalOnMissingBean
	@Bean
	public GitProperties gitProperties(ProjectInfoProperties properties) throws Exception {
		return new GitProperties(
				loadFrom(properties.getGit().getLocation(), "git-core", properties.getGit().getEncoding()));
	}

	protected Properties loadFrom(Resource location, String prefix, Charset encoding) throws IOException {
		prefix = prefix.endsWith(".") ? prefix : prefix + ".";
		Properties source = loadSource(location, encoding);
		Properties target = new Properties();
		for (String key : source.stringPropertyNames()) {
			if (key.startsWith(prefix)) {
				target.put(key.substring(prefix.length()), source.get(key));
			}
		}
		return target;
	}

	private Properties loadSource(Resource location, Charset encoding) throws IOException {
		if (encoding != null) {
			return PropertiesLoaderUtils.loadProperties(new EncodedResource(location, encoding));
		}
		return PropertiesLoaderUtils.loadProperties(location);
	}




}
