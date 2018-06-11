package opca.view;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;

/*
 * We are just creating the class so that we have someplace to hold the mergeSubcodes routine.
 * It gets called by a couple difference classes, it
 */
public class QueueUtility extends ArrayDeque<ViewReference> {
	private static final long serialVersionUID = 1L;
	
	/*
	 * public void mergeSubcode( ArrayDeque<OpinionReference> queue,
	 * ArrayList<OpinionReference> subcodes ) { OpinionReference subcode =
	 * queue.pop(); for ( int i=0, j = subcodes.size(); i<j; ++i ) {
	 * OpinionReference listSubcode = subcodes.get(i); // Right here we are
	 * checking whether the codeSections are the same // and if so, then we
	 * "merge" them, which means only to discard // one and update the reference
	 * count .. if ( listSubcode.getStatutesBaseClass() == subcode.getStatutesBaseClass() )
	 * { listSubcode.incorporateSubcode( subcode, queue ); return; } }
	 * subcodes.add(subcode); subcode.addToChildren(queue); }
	 */
	public ViewReference mergeSubcodes(ArrayList<ViewReference> references) {
		ViewReference opReference = pop();
		Iterator<ViewReference> lit = references.iterator();
		while ( lit.hasNext() ) {
			ViewReference listSubcode = lit.next();
			// Right here we are checking whether the codeSections are the same
			// and if so, then we "merge" them, which means only to discard
			// one and update the reference count ..
			// if ( listSubcode.getStatutesBaseClass() == subcode.getStatutesBaseClass() ) {
			// well, yea, the codeSection has to match, or don't even consider
			if (listSubcode.getStatutesBaseClass().equals(opReference.getStatutesBaseClass())) {

				listSubcode.incorporateOpinionReference( opReference, this );
				return opReference;
			}
		}
		references.add(opReference);
		opReference.addToChildren(this);
		return opReference;
	}
}
