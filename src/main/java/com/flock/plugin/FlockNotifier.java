package com.flock.plugin;

import hudson.Launcher;
import hudson.Extension;
import hudson.model.*;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.BuildStepDescriptor;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import java.io.IOException;

import org.jenkinsci.Symbol;
import org.kohsuke.stapler.StaplerRequest;

public class FlockNotifier extends hudson.tasks.Recorder {

    private String webhookUrl;

    private boolean notifyOnStart;
    private boolean notifyOnSuccess;
    private boolean notifyOnUnstable;
    private boolean notifyOnAborted;
    private boolean notifyOnFailure;
    private boolean notifyOnNotBuilt;

    @DataBoundConstructor
    public FlockNotifier(String webhookUrl, boolean notifyOnStart, boolean notifyOnSuccess, boolean notifyOnUnstable, boolean notifyOnAborted, boolean notifyOnFailure, boolean notifyOnNotBuilt) {
        this.webhookUrl = webhookUrl;
        this.notifyOnStart = notifyOnStart;
        this.notifyOnSuccess = notifyOnSuccess;
        this.notifyOnUnstable = notifyOnUnstable;
        this.notifyOnAborted = notifyOnAborted;
        this.notifyOnFailure = notifyOnFailure;
        this.notifyOnNotBuilt = notifyOnNotBuilt;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public Boolean isNotifyOnStart() { return notifyOnStart; }

    public boolean isNotifyOnSuccess() { return notifyOnSuccess; }

    public boolean isNotifyOnUnstable() { return notifyOnUnstable; }

    public boolean isNotifyOnAborted() { return notifyOnAborted; }

    public boolean isNotifyOnFailure() { return notifyOnFailure; }

    public boolean isNotifyOnNotBuilt() { return notifyOnNotBuilt; }

    @Override
    public boolean prebuild(AbstractBuild<?, ?> build, BuildListener listener) {
        if (build.isBuilding()) {
            listener.getLogger().println("Build started");
        }

        return super.prebuild(build, listener);
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
        if (build.getResult() == Result.SUCCESS) {
            listener.getLogger().println("Build Successful, build number: " + build.number + "!");
        } else {
            listener.getLogger().println("Build Failed, build number: " + build.number + "!");
        }
        return true;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }


    @Symbol("greet")
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "Send Flock Notification";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
            req.bindJSON(this, json);
            save();
            return super.configure(req, json);
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }
}
