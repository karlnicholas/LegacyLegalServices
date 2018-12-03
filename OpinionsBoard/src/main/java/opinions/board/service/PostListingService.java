package opinions.board.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import opinions.board.model.*;

@Stateless
public class PostListingService {
	@Inject private EntityManager em;
	
	public List<BoardPost> getBoardPosts(int count) {
		List<BoardPost> listings = em.createQuery("select bp from BoardPost bp order by bp.date desc", BoardPost.class).setMaxResults(count).getResultList();
		if ( listings == null ) {
			return new ArrayList<>();
		}
		return listings;
	}
	public void createNewBoardPost(BoardPost boardPost) {
		if ( boardPost.getDate() == null ) {
			boardPost.setDate(LocalDateTime.now());
		}
		em.persist(boardPost);
	}
	
	public void deleteBoardPost(BoardPost boardPost) {
//		PostDetailService postDetailService = new PostDetailService(em);
//		postDetailService.deletePostDependents(getBoardPostDetail(boardPost));
		em.remove(em.find(BoardPost.class, boardPost.getId()));
	}
}
