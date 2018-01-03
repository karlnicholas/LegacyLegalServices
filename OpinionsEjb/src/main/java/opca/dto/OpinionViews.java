package opca.dto;

import java.util.ArrayList;
import java.util.List;

public class OpinionViews {
	private final List<OpinionView> opinions;

	public OpinionViews(List<opca.view.OpinionView> opinionCasesForAccount) {
		opinions = new ArrayList<OpinionView>();
		for (opca.view.OpinionView opinionView: opinionCasesForAccount ) {
			opinions.add( new OpinionView(opinionView));
		}
	}

	public List<OpinionView> getOpinions() {
		return opinions;
	}

}
