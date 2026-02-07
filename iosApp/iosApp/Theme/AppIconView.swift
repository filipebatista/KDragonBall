import SwiftUI

/// A SwiftUI view that renders the Dragon Ball 4-star app icon
/// This matches the Android ic_launcher_foreground.xml
struct AppIconView: View {
    var size: CGFloat = 1024

    var body: some View {
        ZStack {
            // Background
            Color(red: 255/255, green: 248/255, blue: 240/255)

            // Dragon Ball sphere - outer
            Circle()
                .fill(Color.dragonBallOrange)
                .frame(width: size * 0.61, height: size * 0.61)

            // Inner glow
            Circle()
                .fill(Color.dragonBallYellow)
                .frame(width: size * 0.52, height: size * 0.52)

            // Four stars (4-star dragon ball)
            Group {
                // Star 1 - top left
                StarShape()
                    .fill(Color(red: 211/255, green: 47/255, blue: 47/255))
                    .frame(width: size * 0.13, height: size * 0.13)
                    .offset(x: -size * 0.1, y: -size * 0.1)

                // Star 2 - top right
                StarShape()
                    .fill(Color(red: 211/255, green: 47/255, blue: 47/255))
                    .frame(width: size * 0.13, height: size * 0.13)
                    .offset(x: size * 0.1, y: -size * 0.1)

                // Star 3 - bottom left
                StarShape()
                    .fill(Color(red: 211/255, green: 47/255, blue: 47/255))
                    .frame(width: size * 0.13, height: size * 0.13)
                    .offset(x: -size * 0.1, y: size * 0.1)

                // Star 4 - bottom right
                StarShape()
                    .fill(Color(red: 211/255, green: 47/255, blue: 47/255))
                    .frame(width: size * 0.13, height: size * 0.13)
                    .offset(x: size * 0.1, y: size * 0.1)
            }

            // Shine effect
            ShineShape()
                .stroke(Color.white, style: StrokeStyle(lineWidth: size * 0.03, lineCap: .round))
                .frame(width: size * 0.15, height: size * 0.08)
                .offset(x: -size * 0.15, y: -size * 0.15)
        }
        .frame(width: size, height: size)
    }
}

struct StarShape: Shape {
    func path(in rect: CGRect) -> Path {
        let center = CGPoint(x: rect.midX, y: rect.midY)
        let outerRadius = min(rect.width, rect.height) / 2
        let innerRadius = outerRadius * 0.4
        let points = 5

        var path = Path()

        for i in 0..<(points * 2) {
            let radius = i.isMultiple(of: 2) ? outerRadius : innerRadius
            let angle = (Double(i) * .pi / Double(points)) - .pi / 2

            let point = CGPoint(
                x: center.x + CGFloat(cos(angle)) * radius,
                y: center.y + CGFloat(sin(angle)) * radius
            )

            if i == 0 {
                path.move(to: point)
            } else {
                path.addLine(to: point)
            }
        }

        path.closeSubpath()
        return path
    }
}

struct ShineShape: Shape {
    func path(in rect: CGRect) -> Path {
        var path = Path()
        path.move(to: CGPoint(x: rect.minX, y: rect.maxY))
        path.addQuadCurve(
            to: CGPoint(x: rect.maxX, y: rect.minY),
            control: CGPoint(x: rect.midX, y: rect.midY - rect.height * 0.3)
        )
        return path
    }
}

#Preview {
    AppIconView(size: 200)
}
