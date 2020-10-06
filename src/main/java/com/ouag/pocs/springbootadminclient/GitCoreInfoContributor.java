package com.ouag.pocs.springbootadminclient;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoPropertiesInfoContributor;
import org.springframework.boot.info.GitProperties;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;
import java.util.Properties;


public class GitCoreInfoContributor extends InfoPropertiesInfoContributor<GitProperties> {

    public GitCoreInfoContributor(GitProperties properties) {
        this(properties, Mode.SIMPLE);
    }

    public GitCoreInfoContributor(GitProperties properties, Mode mode) {
        super(properties, mode);
    }

    @Override
    protected PropertySource<?> toSimplePropertySource() {
        Properties props = new Properties();
        this.copyIfSet(props, "branch");
        String commitId = ((GitProperties)this.getProperties()).getShortCommitId();
        if (commitId != null) {
            props.put("commit.id", commitId);
        }

        this.copyIfSet(props, "commit.time");
        return new PropertiesPropertySource("git-core", props);
    }

    @Override
    public void contribute(Info.Builder builder) {
        builder.withDetail("git-core", this.generateContent());
    }

    protected void postProcessContent(Map<String, Object> content) {
        this.replaceValue(this.getNestedMap(content, "commit"), "time", ((GitProperties)this.getProperties()).getCommitTime());
        this.replaceValue(this.getNestedMap(content, "build"), "time", ((GitProperties)this.getProperties()).getInstant("build.time"));
    }
}
