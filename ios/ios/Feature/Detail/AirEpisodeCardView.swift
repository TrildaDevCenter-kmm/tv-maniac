//
//  AirEpisodeCardView.swift
//  tv-maniac
//
//  Created by Thomas Kioko on 02.02.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI
import TvManiac
import Kingfisher

struct AirEpisodeCardView: View {
	
	var episode: LastAirEpisode
	
	var body: some View {
		VStack(alignment: .leading) {
			
			HStack{
				
				ZStack(alignment: .top) {
					Image(systemName: "bookmark.fill")
						.resizable()
						.font(Font.title.weight(.light))
						.aspectRatio(contentMode: .fit)
						.foregroundColor(Color.grey_500)
						.frame(width: 48, height: 84)
					
					Image(systemName: "plus")
						.resizable()
						.font(Font.title.weight(.light))
						.foregroundColor(Color.grey_900)
						.frame(width: 22, height: 22)
						.padding(.top, 25)
				}
				
				
				VStack(alignment: .leading){
					
					Text(episode.title)
						.titleBoldFont(size: 23)
						.foregroundColor(.white)
						.padding(.leading, -5)
						.background(
							RoundedRectangle(cornerRadius: 5)
								.fill(Color.accent)
							
								.frame(width: 210, height: 40)
							
						)
						.frame(width: 210, height: 40)
					
					
					Text(episode.airDate)
						.bodyFont(size: 18)
						.foregroundColor(Color.text_color_bg)
					
				}
				
				Spacer()
				
			}
			
			Text(episode.name ?? "TBA")
				.titleStyle()
				.lineLimit(1)
				.foregroundColor(Color.text_color_bg)
			
			Text(episode.overview)
				.bodyFont(size: 16)
				.lineLimit(4)
				.foregroundColor(Color.text_color_bg)
				.padding(.top, 1)
			
			Spacer()
		}
		.padding(16)
		.frame(width: 330,height: 200)
		.background(
			RoundedRectangle(cornerRadius: 5)
				.fill(Color.background)
				.shadow(color: .grey_900, radius: 5, x: 0, y: 5)
			
		)
	}
}

struct AirEpisodeCardView_Previews: PreviewProvider {
	static var previews: some View {
		AirEpisodeCardView(episode: episode)
		
		AirEpisodeCardView(episode: episode)
			.preferredColorScheme(.dark)
	}
}