package etorg.gui;

import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;




/**
 * 
 * Not yet used, do not work because of unsolved conflict javax.faces and com.sun.faces
 * 
 * @author Hans Reier Sigmond
 *
 */
public class MyPhaseListener {
		
	public MyPhaseListener() {
		}
	public void beforePhase(PhaseEvent pe) {
		if (pe.getPhaseId() == PhaseId.RESTORE_VIEW) {
			System.out.println("Processing new Request!");
		}
		System.out.println("before - " + pe.getPhaseId().toString());
	}
	
	public void afterPhase(PhaseEvent pe) {
		System.out.println("after - " + pe.getPhaseId().toString());
		if (pe.getPhaseId() == PhaseId.RENDER_RESPONSE) {
			System.out.println("Done with Request+n");
		}
		
	}
	
	public PhaseId getPhaseId() {
		return PhaseId.ANY_PHASE;
	}
}
