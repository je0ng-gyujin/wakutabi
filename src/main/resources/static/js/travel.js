$(document).ready(
		function () {
			// 태그 클릭 이벤트
			$('.tag-item').click(function() {
				// 클릭 시 태그의 활성화/비활성화 상태만 변경
				$(this).toggleClass('active');
				//updateSearch();
			});

			/*// 검색 기능
			$('#searchInput').on('input', function() {
				updateSearch();
			});*/

				
			/*// 검색 버튼 클릭 이벤트
			$('.btn-wakutabi-primary').click(function() {
			    updateSearch();
			});*/
			
			// 필터 변경 감지
			$('input[type="checkbox"], select').change(
				function() {
					updateSearch();
				});

			function updateSearch() {
				// 선택된 태그들
				var selectedTags = [];
				$('.tag-item.active').each(function() {
					selectedTags.push($(this).data('tag'));
				});
				
				

				// 검색어
				var searchQuery = $('#searchInput').val();

				// 실제 구현에서는 AJAX로 서버에 요청
				/*console.log('검색 조건:', {
					tags: selectedTags,
					query: searchQuery,
					filters: getFilterValues()
				});

				// 검색 결과 업데이트 (예시)
				updateResultCount(selectedTags.length,
					searchQuery);*/
					
					// 카드 필터링
					    $('.schedule-card').each(function () {
					        var cardText = $(this).text().toLowerCase();
					        var matchesQuery = searchQuery.length === 0 || cardText.includes(searchQuery);

					        // 태그 조건 (선택된 태그가 없으면 통과, 있으면 하나라도 포함되어야 함)
					        var matchesTags = selectedTags.length === 0;
					        if (!matchesTags) {
					            matchesTags = selectedTags.some(tag => cardText.includes(tag.toLowerCase()));
					        }

					        if (matchesQuery && matchesTags) {
					            $(this).show();
					        } else {
					            $(this).hide();
					        }
					    });

					    // 결과 개수 갱신
					    var visibleCount = $('.schedule-card:visible').length;
					    $('.search-result-count').text('총 ' + visibleCount + '개');
					}
			

			function getFilterValues() {
				var filters = {
					regions: [],
					duration: $('select').first().val(),
					difficulties: [],
					groupSizes: []
				};

				// 지역 필터
				$('input[type="checkbox"]:checked')
					.each(
						function() {
							var id = $(this).attr(
								'id');
							if (['tokyo', 'osaka',
								'kyoto',
								'hiroshima']
								.includes(id)) {
								filters.regions
									.push(id);
							} else if (['easy',
								'medium',
								'hard']
								.includes(id)) {
								filters.difficulties
									.push(id);
							} else if (['small',
								'medium-group',
								'large']
								.includes(id)) {
								filters.groupSizes
									.push(id);
							}
						});

				return filters;
			}

			function updateResultCount(tagCount, query) {
				var baseCount = 24;
				var newCount = Math.max(1, baseCount
					- (tagCount * 3)
					- (query.length > 0 ? 5 : 0));
				$('.search-result-count').text(
					'총 ' + newCount + '개');
			}

			// 카드 호버 효과
			$('.schedule-card')
				.hover(
					function() {
						$(this)
							.find('.btn')
							.removeClass(
								'btn-wakutabi-outline')
							.addClass(
								'btn-wakutabi-primary');
					},
					function() {
						$(this)
							.find('.btn')
							.first()
							.removeClass(
								'btn-wakutabi-primary')
							.addClass(
								'btn-wakutabi-outline');
					});
					
					// 🔥 검색폼 submit 시, 필터값을 같이 넣어주기
					  $("#searchForm").on("submit", function (e) {
					    const filterForm = $("#filterForm"); // 사이드바 필터 form
					    const searchForm = $(this); // 검색 form

					    // filterForm 안의 input, select, checkbox 값들을 검색 form에 hidden으로 복사
					    filterForm.find("input, select").each(function () {
					      if (this.name && this.value) {
					        // 이미 searchForm에 같은 name이 있으면 추가 안함
					        if (searchForm.find(`[name='${this.name}']`).length === 0) {
					          $("<input>").attr({
					            type: "hidden",
					            name: this.name,
					            value: this.value
					          }).appendTo(searchForm);
					        }
					      }
					    });
					  });
					  document.addEventListener('DOMContentLoaded', function () {
					  		     function initLoadMore(sectionId, buttonId, step = 5) {
					  		       const container = document.getElementById(sectionId);
					  		       const btn = document.getElementById(buttonId);

					  		       if (!container) {
					  		         console.warn(`[LoadMore] container not found: ${sectionId}`);
					  		         return;
					  		       }
					  		       if (!btn) {
					  		         console.warn(`[LoadMore] button not found: ${buttonId}`);
					  		         return;
					  		       }

					  		       const cards = Array.from(container.querySelectorAll('.schedule-card'));
					  		       console.log(`[LoadMore] ${sectionId} found schedule-card count =`, cards.length);

					  		       if (cards.length <= step) {
					  		         // 카드가 step 이하이면 버튼 숨김, 카드 모두 보이게
					  		         cards.forEach(c => {
					  		           const wrap = c.closest('.col-12') || c.parentElement;
					  		           if (wrap) wrap.classList.remove('hidden-card');
					  		         });
					  		         btn.style.display = 'none';
					  		         return;
					  		       }

					  		       // 처음엔 모두 숨기기
					  		       cards.forEach(c => {
					  		         const wrap = c.closest('.col-12') || c.parentElement;
					  		         if (wrap) wrap.classList.add('hidden-card');
					  		       });

					  		       let visible = 0;
					  		       function showMore() {
					  		         const next = Math.min(visible + step, cards.length);
					  		         for (let i = visible; i < next; i++) {
					  		           const wrap = cards[i].closest('.col-12') || cards[i].parentElement;
					  		           if (wrap) wrap.classList.remove('hidden-card');
					  		         }
					  		         visible = next;
					  		         btn.style.display = (visible < cards.length) ? 'inline-block' : 'none';
					  		         console.log(`[LoadMore] ${sectionId} visible = ${visible}/${cards.length}`);
					  		       }

					  		       // 초기 노출
					  		       showMore();

					  		       // 버튼 이벤트
					  		       btn.addEventListener('click', showMore);
					  		     }

					  		     // 두 섹션 초기화 (id 이름이 다르면 여기만 바꾸면 됩니다)
					  		     initLoadMore('registeredTrips', 'loadMoreRegistered', 5);
					  		     initLoadMore('appliedTrips', 'loadMoreApplied', 5);
					  		   });
					  			
					  		   
							   // travel.js 파일 내부에 추가 (또는 기존 '신청 관리' 로직 대체)

							   // 1. '신청 관리' 버튼 클릭 이벤트 리스너
							   $(document).on('click', '.btn-manage', function(e) {
							       // 버튼이 눌렸을 때 토글되는 영역을 찾습니다.
							       const tripArticleId = $(this).data('trip-id');
							       const container = $(`#applicant-container-${tripArticleId}`);

							       // 만약 컨테이너가 이미 펼쳐져 있다면 닫고 내용 지우기
							       if (container.hasClass('show') && container.html().trim() !== '') {
							           container.slideUp(200, function() {
							               $(this).empty().removeClass('show');
							           });
							           return; 
							       }
							       
							       // 닫혀 있다면 펼치면서 신청자 목록을 가져오는 AJAX 요청 함수 호출
							       fetchApplicants(tripArticleId, container);
							   });


							   // 2. 신청자 목록을 AJAX로 가져와서 화면에 렌더링하는 함수
							   function fetchApplicants(tripArticleId, container) {
							       // 로딩 스피너 표시
							       container.html(`
							           <div class="card border p-3 text-center text-muted">
							               <div class="spinner-border spinner-border-sm me-2" role="status">
							                   <span class="visually-hidden">Loading...</span>
							               </div>
							               신청자 목록을 불러오는 중입니다...
							           </div>
							       `).slideDown(200, function() {
							           $(this).addClass('show'); // 컨테이너가 펼쳐진 상태임을 표시
							       });

							       const apiUrl = `/schedule/api/schedule/${tripArticleId}/applicants`; 
							       
							       $.ajax({
							           url: apiUrl,
							           type: 'GET',
							           dataType: 'json', // 서버에서 JSON을 기대합니다.
							           success: function(applicants) {
							               // 3. 성공 시, HTML을 생성하여 영역에 삽입
							               renderApplicants(applicants, container, tripArticleId);
							           },
							           error: function(xhr, status, error) {
							               console.error("신청자 목록 로딩 실패:", error);
							               container.html(`
							                   <div class="alert alert-danger mt-2" role="alert">
							                       신청자 목록을 불러오는 데 실패했습니다. 잠시 후 다시 시도해 주세요.
							                   </div>
							               `);
							           }
							       });
							   }

							   // 3. 신청자 목록을 HTML로 변환하여 렌더링하는 함수
							   function renderApplicants(applicants, container, tripArticleId) {
							       
							       // 신청자 수 표시 업데이트 (Optional: 필요하다면 신청자 수를 따로 업데이트)
							       // $(`#applicant-count-${tripArticleId}`).text(applicants.length);

							       if (applicants.length === 0) {
							           container.html(`
							               <div class="card border p-3 text-center text-muted">
							                   현재 대기 중인 신청자가 없습니다.
							               </div>
							           `);
							           return;
							       }

							       // 신청자 리스트를 순회하며 HTML 문자열 생성
							       const applicantRows = applicants.map(app => `
							           <div class="applicant-row d-flex justify-content-between align-items-center mb-2 p-2 border-bottom" data-request-id="${app.requestId}">
							               <div class="applicant-info d-flex align-items-center me-3">
							                   <img src="${app.profileUrl || '/image/default_profile.png'}" 
							                        alt="${app.nickname}" 
							                        class="rounded-circle me-2" 
							                        style="width: 30px; height: 30px; object-fit: cover;">
							                   
							                   <div>
							                       <strong>${app.nickname}</strong>
							                       <span class="text-muted ms-2 small">
							                           (${app.gender === 'M' ? '남성' : '여성'}, ${app.age}세)
							                       </span>
							                       <div class="small text-break">${app.introduce || '자기소개 없음'}</div>
							                   </div>
							               </div>
							               
							               <div class="applicant-actions d-flex flex-shrink-0">
							                   <button class="btn btn-success btn-sm me-2 btn-applicant-action" 
							                           data-request-id="${app.requestId}" 
							                           data-action="ACCEPT">수락</button>
							                   <button class="btn btn-danger btn-sm btn-applicant-action" 
							                           data-request-id="${app.requestId}" 
							                           data-action="REJECT">거절</button>
							               </div>
							           </div>
							       `).join('');

							       const finalHtml = `
							           <div class="card border p-3 mt-2">
							               <h6 class="mb-3">대기 중인 신청자 목록 (${applicants.length}명)</h6>
							               ${applicantRows}
							           </div>
							       `;

							       container.html(finalHtml);
							   }
		});
		
		document.querySelectorAll('.region-item').forEach(item => {
		  item.addEventListener('click', () => {
		    item.classList.toggle('active'); // 여러 개 선택 가능
		  });
		});
		
		const startDate = document.getElementById("startDate");
		   const endDate = document.getElementById("endDate");
		   const selectedPeriod = document.getElementById("selectedPeriod");

		   startDate.addEventListener("change", () => {
		     endDate.min = startDate.value; // 출발일 이후만 선택 가능
		     updateSelectedPeriod();
		   });

		   endDate.addEventListener("change", () => {
		     startDate.max = endDate.value; // 도착일 이전까지만 선택 가능
		     updateSelectedPeriod();
		   });

		   function updateSelectedPeriod() {
		     if (startDate.value && endDate.value) {
		       selectedPeriod.textContent = `${startDate.value} ~ ${endDate.value}`;
		     }
		   }
		   
		   var slider = document.getElementById('slider');

		   noUiSlider.create(slider, {
		     start: [0, 2000000], // 초기값을 0~2000000으로 설정
		     connect: true,
		     range: {
		       'min': 0,
		       'max': 2000000
		     }
		   });

		   var minInput = document.getElementById('minPrice');
		   var maxInput = document.getElementById('maxPrice');

		   // 슬라이더 → input 값 반영
		   slider.noUiSlider.on('update', function(values, handle) {
		     var value = Math.round(values[handle]);
		     if (handle === 0) {
		       minInput.value = value > 0 ? value : "";  // 0이면 비워둠
		     } else {
		       maxInput.value = value < 2000000 ? value : ""; // 최대값이면 비워둠
		     }
		   });

		   // input → 슬라이더 값 반영
		   minInput.addEventListener('change', function() {
		     slider.noUiSlider.set([this.value || 0, null]);
		   });

		   maxInput.addEventListener('change', function() {
		     slider.noUiSlider.set([null, this.value || 2000000]);
		   });

		   let selectedRegions = [];

		   document.querySelectorAll(".region-item").forEach(btn => {
		     btn.addEventListener("click", () => {
		       const value = btn.value;

		       if (selectedRegions.includes(value)) {
		         // 이미 선택된 경우 → 해제
		         selectedRegions = selectedRegions.filter(r => r !== value);
		         btn.classList.remove("btn-primary", "text-white");
		       } else {
		         // 새로 선택
		         selectedRegions.push(value);
		         btn.classList.add("btn-primary", "text-white");
		       }

		       // hidden input에 값 넣기 (쉼표 구분)
		       document.getElementById("regionInput").value = selectedRegions.join(",");
		     });
		   });

		   let selectedTags = [];

		   document.querySelectorAll(".tag-item").forEach(tag => {
		     tag.addEventListener("click", () => {
		       const tagValue = tag.dataset.tag;
		       if (selectedTags.includes(tagValue)) {
		         selectedTags = selectedTags.filter(t => t !== tagValue);
		         tag.classList.remove("bg-primary", "text-white");
		       } else {
		         selectedTags.push(tagValue);
		         tag.classList.add("bg-primary", "text-white");
		       }
		       document.getElementById("tagsInput").value = selectedTags.join(",");
		     });
		   });
		   
		   // 4. 수락/거절 버튼 클릭 이벤트 리스너
		   $(document).on('click', '.btn-applicant-action', function() {
		       const button = $(this);
		       const requestId = button.data('request-id');
		       const action = button.data('action'); // 'ACCEPT' 또는 'REJECT'
		       const row = button.closest('.applicant-row'); // 신청자 정보가 담긴 행

		       if (!confirm(`정말로 이 신청을 ${action === 'ACCEPT' ? '수락' : '거절'}하시겠습니까?`)) {
		           return;
		       }
		       
		       // 버튼 비활성화 (중복 클릭 방지)
		       button.prop('disabled', true).text('처리 중...');

		       // 💡 서버에 요청을 보낼 API 경로 (예시: PUT 요청으로 상태 변경)
		       const apiUrl = `/schedule/api/request/${requestId}/status`; 
		       
		       $.ajax({
		           url: apiUrl,
		           type: 'PUT', // 상태 변경은 PUT 또는 POST를 사용합니다.
		           contentType: 'application/json',
		           data: JSON.stringify({ status: action }), // 서버에 'ACCEPT' 또는 'REJECT' 상태를 보냅니다.
		           
		           // Spring Security 사용 시 CSRF 토큰을 헤더에 포함해야 합니다.
		           // beforeSend: function(xhr) {
		           //     xhr.setRequestHeader('X-CSRF-TOKEN', $('meta[name="_csrf"]').attr('content'));
		           // },
		           
		           success: function(response) {
		               alert(`신청이 성공적으로 ${action === 'ACCEPT' ? '수락' : '거절'} 처리되었습니다.`);
		               
		               // 5. 처리 성공 후, 화면에서 해당 행 제거
		               row.fadeOut(300, function() {
		                   $(this).remove();
		                   
		                   // (Optional) 처리 후 남은 신청자 수에 따라 제목 업데이트 로직 추가 가능
		               });
		               
		               // (Optional) 수락 처리 시, 메인 카드에 있는 '참여 중' 인원 수를 +1 업데이트하는 로직 추가
		               if (action === 'ACCEPT') {
		                   updateParticipantCount(row.closest('.schedule-card')); 
		               }
		           },
		           error: function(xhr, status, error) {
		               console.error("신청 처리 실패:", error);
		               alert(`신청 처리 중 오류가 발생했습니다: ${xhr.responseJSON ? xhr.responseJSON.message : '서버 오류'}`);
		               // 실패 시 버튼을 다시 활성화
		               button.prop('disabled', false).text(action === 'ACCEPT' ? '수락' : '거절');
		           }
		       });
		   });

		   // (Optional) 카드 참여 인원 수 업데이트 함수
		   function updateParticipantCount(cardElement) {
		       const countSpan = cardElement.find('.js-participants-toggle span');
		       let currentText = countSpan.text().match(/(\d+)\/(\d+)/);
		       
		       if (currentText && currentText.length === 3) {
		           let current = parseInt(currentText[1]);
		           let max = parseInt(currentText[2]);
		           
		           if (current < max) {
		               current += 1;
		               countSpan.text(`참여 중 ${current}/${max}명`);
		           }
		       }
		   }