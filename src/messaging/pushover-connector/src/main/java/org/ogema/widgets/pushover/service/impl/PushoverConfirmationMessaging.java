package org.ogema.widgets.pushover.service.impl;

import de.iwes.widgets.api.messaging.Message;
import de.iwes.widgets.api.messaging.MessagePriority;
import de.iwes.widgets.api.services.MessagingService;
import de.iwes.widgets.api.widgets.localisation.OgemaLocale;
import java.util.ResourceBundle;
import org.ogema.core.application.ApplicationManager;
import org.ogema.widgets.pushover.model.EmergencyMessage;
import org.slf4j.Logger;

/**
 *
 * @author jlapp
 */
public class PushoverConfirmationMessaging {

    private final Logger logger;

    private static final String ID = "Pushover Confirmations";
    private final ApplicationManager appman;
    private final MessagingService messaging;

    public PushoverConfirmationMessaging(ApplicationManager appman, MessagingService messaging) {
        this.appman = appman;
        this.messaging = messaging;
        logger = appman.getLogger();
    }

    public void start() {
        messaging.registerMessagingApp(appman.getAppID(), ID);
    }

    public void stop() {
        messaging.unregisterMessagingApp(appman.getAppID());
    }

    public void messageConfirmed(EmergencyMessage em) {
        if (!em.receiptInfo().isAcknowledged()) {
            logger.warn("message {} is not acknowledged, ignoring call", em.getLocation());
            return;
        }
        String pTitle = em.title().getValue();
        String pDevice = em.receiptInfo().acknowledgedByDevice().getValue();
        Message msg = new PushoverConfirmationMessaging.ResourceBundleMessage(
                "org.ogema.widgets.pushover.service.impl.Messages",
                PushoverConfirmationMessaging.class.getClassLoader(), "", MessagePriority.MEDIUM)
                .withTitleParams(pTitle, pDevice)
                .withMessageParams(pTitle, pDevice, em.message().getValue());
        messaging.sendMessage(appman.getAppID(), msg);
    }

    static class ResourceBundleMessage implements Message {

        final String basename;
        final ClassLoader loader;
        final String link;
        final MessagePriority prio;
        Object[] titleParams;
        Object[] messageParams;

        public ResourceBundleMessage(String basename, ClassLoader loader, String link, MessagePriority prio) {
            this.basename = basename;
            this.loader = loader;
            this.link = link;
            this.prio = prio;
        }

        public ResourceBundleMessage withTitleParams(Object... params) {
            this.titleParams = params;
            return this;
        }

        public ResourceBundleMessage withMessageParams(Object... params) {
            this.messageParams = params;
            return this;
        }

        @Override
        public String link() {
            return link;
        }

        @Override
        public String message(OgemaLocale locale) {
            if (locale == null) {
                locale = OgemaLocale.ENGLISH;
            }
            ResourceBundle rb = ResourceBundle.getBundle(basename, locale.getLocale(), loader);
            String msg = rb.getString("message");
            return messageParams != null
                    ? String.format(locale.getLocale(), msg, messageParams)
                    : msg;
        }

        @Override
        public String title(OgemaLocale locale) {
            if (locale == null) {
                locale = OgemaLocale.ENGLISH;
            }
            ResourceBundle rb = ResourceBundle.getBundle(basename, locale.getLocale(), loader);
            String msg = rb.getString("title");
            return titleParams != null
                    ? String.format(locale.getLocale(), msg, titleParams)
                    : msg;
        }

        @Override
        public MessagePriority priority() {
            return prio;
        }

    }

}
