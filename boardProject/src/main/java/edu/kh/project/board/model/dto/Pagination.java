package edu.kh.project.board.model.dto;

/** Pagination : 목록을 일정 페이지로 분할해서 원하는 페이지를 볼 수 있게 하는 것. 즉, 페이징 처리
 * pagination 객체 : 페이징 처리에 필요한 값을 모아두고, 계산하는 객체
 */

public class Pagination {
	
	private int currentPage;		// 현재 페이지 번호(cp 이용)
	private int listCount;			// 전체 게시글 수
	
	private int limit = 10;			// 한 페이지 목록에 보여지는 게시글 수
	private int pageSize = 10;		// 보여질 페이지 번호 개수
	
	// 가장 첫페이지는 당연히 1페이지 -> 그래서 minPage는 따로없음
	private int maxPage;			// 마지막 페이지 번호
	private int startPage;			// 보여지는 맨 앞 페이지 번호
	private int endPage;			// 보여지는 맨 뒤 페이지 번호
	
	private int prevPage;			// 이전 페이지 모음의 마지막 번호
	private int nextPage;			// 다음 페이지 모음의 시작 번호
	
	// lombok 을 쓰지 않는 이유는 만들면 안 되는 필드가 있으며,
	// setter나 매개변수 생성자에서 호출해줘야 하는 메서드도 있기 때문
	
	// 기본 생성자 필요 없는 이유
	 // 매개변수가 없으면 계산할 이유가 없기 때문에 만들지 않음
	
	// 1. 매개변수 생성자 2개짜리(currentPage, listCount)
	public Pagination(int currentPage, int listCount) {
		super();
		this.currentPage = currentPage;
		this.listCount = listCount;
		
		calculate();
		
		// 현재 클래스로 pagination 객체 생성 시, cp, listCount(전체 게시글 수)를 받아서 객체를 생성하고,
		// cp, listcount 는 계속 변경될 것이고
		// 이 값에 따라 calculate 가 실행되어 계산될 것 
	}

	// 2. 매개변수 생성자 4개짜리(currentPage, listCount, limit, pageSize)
	public Pagination(int currentPage, int listCount, int limit, int pageSize) {
		super();
		this.currentPage = currentPage;
		this.listCount = listCount;
		this.limit = limit;
		this.pageSize = pageSize;
		
		calculate();
	}
	
	// 3. getter 는 전부 만든다
	public int getCurrentPage() {
		return currentPage;
	}

	public int getListCount() {
		return listCount;
	}

	public int getLimit() {
		return limit;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getMaxPage() {
		return maxPage;
	}

	public int getStartPage() {
		return startPage;
	}

	public int getEndPage() {
		return endPage;
	}

	public int getPrevPage() {
		return prevPage;
	}

	public int getNextPage() {
		return nextPage;
	}
	
	// 4. setter 는 4개만 만든다
	 // 개별 setter 에 전부 calculate() 적용해준다.
	 // setter의 의미는 결국 필드의 값을 새로 설정해준다는 의미이고, 
	 // 들어오는 값이 변하면 결국 새로 계산해줘야 하므로
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
		calculate();
	}

	public void setListCount(int listCount) {
		this.listCount = listCount;
		calculate();
	}

	public void setLimit(int limit) {
		this.limit = limit;
		calculate();
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
		calculate();
	}
	
	// 5. toString
	@Override
	public String toString() {
		return "Pagination [currentPage=" + currentPage + ", listCount=" + listCount + ", limit=" + limit
				+ ", pageSize=" + pageSize + ", maxPage=" + maxPage + ", startPage=" + startPage + ", endPage="
				+ endPage + ", prevPage=" + prevPage + ", nextPage=" + nextPage + "]";
	}
	
	// 6. 계산 메서드 작성
	
	/** 페이징 처리에 필요한 값을 계산해서 필드에 대입하는 메서드
	 	StartPage, EndPage, maxPage, prevPage, nextPage
	 	위 5개 변수의 값은 해당 메서드 이용하여 값을 계속 바꿔주어야 함
	 */
	private void calculate() {
		
		// 1.
		// maxPage : 최대 페이지, 또는 마지막 페이지, 또는 총 페이지 수
		// 만약 limitPage 가 10개인 상황에서 글이 110개이면 maxPage = 11개
		// 		limitPage 가 10개인 상황에서 글이 102개이면 maxPage = 11개
		// 	    limitPage 가 10개인 상황에서 글이 111개이면 maxPage = 12개
		
		maxPage = (int)Math.ceil((double)listCount/limit);
		
		// 1) listCount = 95, limit = 10 일 때, listCount/limit = 9 (java에서 나머지 처리 안함)
		// 2) (double)형변환 후 > 9.5
		// 3) Math.ceil 을 통해 올림 처리 > 10.0
		// 4) int로 강제형변환 > 10
		// * 정답은 아님. 결과만 같으면 됨
		
		// 2. 
		// StartPage : 페이지 번호 목록의 시작 번호
		// currentPage 가 1~10 사이 : startPage = 1
		// currentPage 가 11~20 사이 : startPage = 11
		
		startPage = (currentPage - 1) / pageSize * pageSize + 1;
		
		// 3.
		// endPage : 페이지 번호 목록의 끝 번호
		
		endPage = (((currentPage - 1) / pageSize) + 1) * pageSize; 
		// 강사님 작성 부분 : endPage = pageSize - 1 + startPage;
		
		// 3-1.
		// endPage 는 마지막 글의 마지막 페이지일 때 경우 생각해줘야 함
		// 페이지 끝 번호가 최대 페이지 수 초과한 경우,
		// 만약 현재 15페이지이고 게시글 끝의 마지막 페이지가 18페이지일 때
	    // 조건문이 없다면 endPage = 20으로 계산되며  
		// 이때, endPage(20)이 maxPage(18)을 초과하게 된다.
		// 따라서 endPage를 maxPage로 맞춰준다. 
		if(endPage > maxPage) endPage = maxPage;
		
		// 4.
		// prevPage : "<" 클릭 시 이동, 이전 레벨 페이지 번호 목록 중 마지막 번호
		// nextPage : ">" 클릭 시 이동, 이후 레벨 페이지 번호 목록 중 시작 번호
		
		// 4-1. 조건 따져줘야 함
		
		// prevPage의 경우
		 	// currentPage 가 pageSize(10) 이하일 때 즉, 1~10페이지 일 때 더 이상 이전으로 갈 페이지가 없으므로
			if(currentPage <= pageSize) {
				prevPage = 1;
			} else { // 그게 아니라면, 
				prevPage = startPage - 1;
			}
			
		// nextPage의 경우
			// 만약 현재페이지 레벨의 마지막 페이지(endPage) 가 게시글 끝 마지막 페이지(maxPage)와 같을 때,
			// nextPage 를 눌러도 게시글 끝 마지막 페이지(maxPage)로 이동해야 함
			if(endPage == maxPage) {
				nextPage = maxPage;
			}else { // 그게 아니라면,
				nextPage = endPage + 1;
			}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
}
	