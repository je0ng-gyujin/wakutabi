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