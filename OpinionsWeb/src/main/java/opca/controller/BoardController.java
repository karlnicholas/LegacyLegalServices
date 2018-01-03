package opca.controller;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import opca.model.Article;
import opca.model.ArticleComment;
import opca.model.User;
import opca.service.ArticleService;
import opca.service.UserService;

@ManagedBean
@ViewScoped
@SuppressWarnings("serial")
public class BoardController implements Serializable {
//    private static final String NAV_BOARD = "/views/board/board.xhtml"; 

    @Inject private FacesContext facesContext;
    @Inject private ArticleService articleService;
    @Inject private UserService userService;
    private Long openArticle;
    private Long openComment;
    private String comment;
    private User currentUser;
    

    private List<Article> articles;
    
    @PostConstruct
    public void postConstruct() {
    	currentUser = AccountsController.getCurrentUser(facesContext, userService);
    	articles = articleService.getArticles();
    	ExternalContext externalContext = facesContext.getExternalContext();
    	// This insures the current user is available as a field.
        currentUser = (User)externalContext.getSessionMap().get("user");
    }
    
    public String postComment() {
    	if ( currentUser == null ) {
    		return AccountsController.NAV_ACCOUNTS;
    	}
    	if ( comment != null && !comment.isEmpty() ) {
    		Article article = findArticleById(openArticle);
    		articleService.postComment(article, currentUser, comment);
        	articles = articleService.getArticles();
    	}
		openComment = null;
		comment = null;
    	return null;
    }
    public String upvoteComment(Long commentId) {
    	if ( currentUser == null ) {
    		return AccountsController.NAV_ACCOUNTS;
    	}
    	updateCommentImportance(commentId, 1);
		return null;
    }
    public String downvoteComment(Long commentId) {
    	if ( currentUser == null ) {
    		return AccountsController.NAV_ACCOUNTS;
    	}
    	updateCommentImportance(commentId, -1);
		return null;
    }
    private void updateCommentImportance(Long commentId, int amount) {
    	Article article = findArticleById(openArticle);
		for ( ArticleComment comment: article.getArticleComments()) {
			if ( comment.getId().equals(commentId)) {
				int importance = comment.getImportance();
				importance += amount;
				if ( importance < 1 || importance > 4 ) {
					importance = comment.getImportance();
				}
				comment.setImportance(importance);
				articleService.mergeComment(comment);
				return;
			}
		}
	}
    private Article findArticleById(Long articleId) {
    	for ( Article article: articles ) {
    		if ( article.getId().equals(articleId)) return article;
    	}
    	throw new IllegalArgumentException("Unknown article id: " + articleId);
    }
	public List<Article> getArticles() {
    	return articles;
    }
    
    public void openArticle(Long id) {
    	openArticle = id;
    }
    public void closeArticle() {
    	openArticle = null;
    }
    public boolean articleOpen(Long id) {
    	return (openArticle != null && openArticle.equals(id) );
    }

    public String openComment(Long id) {
    	if ( currentUser == null ) {
    		return AccountsController.NAV_ACCOUNTS;
    	}
    	openComment = id;
    	openArticle = id;
    	return null;
    }
    public void closeComment() {
    	openComment = null;
    }
    public boolean commentOpen(Long id) {
    	return (openComment != null && openComment.equals(id) );
    }
	//TODO: does this belong in JsfUtils?
	public List<Integer> repeatNTimes(int count) {
		List<Integer> result = new ArrayList<Integer>();
		for (int i=0; i < count; ++i) {
			result.add(i);
		}
		return result;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public String loginURL() {
		return AccountsController.NAV_ACCOUNTS;
	}
}
