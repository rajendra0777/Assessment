package com.caprae.saasquatch.dto;

import java.util.List;

public class OutreachResponse {
    private String subjectLine;
    private String emailBody;
    private String followUpDay3Snippet;
    private String followUpDay7Snippet;
    private String personalizationHook;
    private List<String> strategicDiscussionPoints;

    public OutreachResponse() {}

    public String getSubjectLine() { return subjectLine; }
    public void setSubjectLine(String subjectLine) { this.subjectLine = subjectLine; }

    public String getEmailBody() { return emailBody; }
    public void setEmailBody(String emailBody) { this.emailBody = emailBody; }

    public String getFollowUpDay3Snippet() { return followUpDay3Snippet; }
    public void setFollowUpDay3Snippet(String followUpDay3Snippet) { this.followUpDay3Snippet = followUpDay3Snippet; }

    public String getFollowUpDay7Snippet() { return followUpDay7Snippet; }
    public void setFollowUpDay7Snippet(String followUpDay7Snippet) { this.followUpDay7Snippet = followUpDay7Snippet; }

    public String getPersonalizationHook() { return personalizationHook; }
    public void setPersonalizationHook(String personalizationHook) { this.personalizationHook = personalizationHook; }

    public List<String> getStrategicDiscussionPoints() { return strategicDiscussionPoints; }
    public void setStrategicDiscussionPoints(List<String> strategicDiscussionPoints) { this.strategicDiscussionPoints = strategicDiscussionPoints; }
}
