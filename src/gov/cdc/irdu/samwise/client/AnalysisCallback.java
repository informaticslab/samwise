package gov.cdc.irdu.samwise.client;

import gov.cdc.irdu.samwise.shared.ReadabilityScores;

/**
 * 
 * @author <a href="mailto:jmrives@spiral-soft.com">Joel M. Rives</a>
 * Created on Feb 21, 2011
 *
 */
public interface AnalysisCallback
{
    void onCancel();
    
    void onOk(ReadabilityScores scores);
    
}
