package com.msxt.booking.exceptioncontrol;

import java.io.IOException;

import javax.enterprise.context.NonexistentConversationException;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.jboss.solder.logging.Logger;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.HandlesExceptions;

/**
 * handler those exceptions generated by conversations management
 *
 * @author <a href="http://community.jboss.org/people/spinner">Jose Freitas</a>
 */

@HandlesExceptions
public class ConversationExceptionHandler {
    @Inject
    private FacesContext facesContext;

    /**
     * Handles the exception thrown at the end of a conversation redirecting
     * the flow to a pretty page instead of printing a stacktrace on the screen.
     *
     * @param event
     * @param log
     */
    public void conversationEndedExceptionHandler(@Handles CaughtException<NonexistentConversationException> event, Logger log) {
        log.info("Conversation ended: " + event.getException().getMessage());
        try {
            facesContext.getExternalContext().redirect("conversation_ended");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
