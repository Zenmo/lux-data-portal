import {FunctionComponent, ReactNode} from "react"
import {Link} from "react-router-dom"
import {css} from "@emotion/react"
import {linksToOtherPlaces} from "./core/data"
import {BsArrowRight, BsBoxArrowUpRight, BsFileEarmarkText, BsFillLightbulbFill, BsMap} from "react-icons/bs"

type PageCard = {
    number: string
    to: string
    title: string
    description: string
    icon: ReactNode
}

const pageCards: PageCard[] = [
    {
        number: "01",
        to: "/surveys",
        title: "Uitvragen beheren",
        description: "Bekijk, filter en beheer de ingediende energiedata-uitvragen van bedrijven per project.",
        icon: <BsFileEarmarkText />,
    },
    {
        number: "02",
        to: "/proof-of-concept",
        title: "Oud proof of concept \"simuleer je buurt\"",
        description: "Kies een buurt, verken gebouwdata op de kaart en start een AnyLogic-energiesimulatie.",
        icon: <BsMap />,
    },
]

const pageStyle = css({
    backgroundColor: "#ede9e2",
    minHeight: "calc(100vh - 74px)",
    padding: "0",
})

const sectionDescStyle = css({
    fontSize: "0.9rem",
    color: "#6b7280",
    marginBottom: "1.5rem",
    lineHeight: 1.6,
    maxWidth: "520px",
})

const innerStyle = css({
    maxWidth: "1100px",
    margin: "0 auto",
    padding: "2.5rem 2rem",
})


const headingStyle = css({
    fontSize: "3rem",
    fontWeight: 700,
    color: "#1e3a5f",
    marginBottom: "2.5rem",
    lineHeight: 1.1,
})

const labelTextStyle = css({
    fontSize: "0.7rem",
    fontWeight: 700,
    letterSpacing: "0.15em",
    textTransform: "uppercase",
    color: "#9ca3af",
    marginBottom: "1.25rem",
})

const mainGridStyle = css({
    display: "flex",
    gap: "2rem",
    alignItems: "flex-start",
    "@media (max-width: 768px)": {
        flexDirection: "column",
    },
})

const cardsColumnStyle = css({
    flex: "1 1 0",
    display: "grid",
    gridTemplateColumns: "repeat(2, 1fr)",
    gap: "1.25rem",
    "@media (max-width: 640px)": {
        gridTemplateColumns: "1fr",
    },
})

const cardStyle = css({
    backgroundColor: "#ffffff",
    borderRadius: "0.75rem",
    padding: "1.75rem",
    border: "1px solid #e5e7eb",
    display: "flex",
    flexDirection: "column",
    gap: "0.75rem",
    textDecoration: "none",
    transition: "border-color 0.15s ease-in-out",
    "&:hover": {
        borderColor: "#1e3a5f",
    },
})

const cardNumberStyle = css({
    fontSize: "2.25rem",
    fontWeight: 700,
    color: "#d1c7bb",
    lineHeight: 1,
    marginBottom: "0.25rem",
})

const iconContainerStyle = css({
    width: "2.5rem",
    height: "2.5rem",
    borderRadius: "0.5rem",
    backgroundColor: "#f3f0ec",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    fontSize: "1.1rem",
    color: "#6b7280",
})

const cardTitleStyle = css({
    fontSize: "1rem",
    fontWeight: 600,
    color: "#1f2937",
    margin: 0,
})

const cardDescStyle = css({
    fontSize: "0.875rem",
    color: "#6b7280",
    margin: 0,
    flexGrow: 1,
})

const cardFooterStyle = css({
    display: "inline-flex",
    alignItems: "center",
    gap: "0.35rem",
    fontSize: "0.875rem",
    fontWeight: 500,
    color: "#2563eb",
    marginTop: "0.25rem",
})

const linksColumnStyle = css({
    width: "280px",
    flexShrink: 0,
    "@media (max-width: 768px)": {
        width: "100%",
    },
})

const linksPanelStyle = css({
    backgroundColor: "#ffffff",
    borderRadius: "0.75rem",
    padding: "1.5rem",
    border: "1px solid #e5e7eb",
})

const linksPanelTitleStyle = css({
    fontWeight: 600,
    color: "#1f2937",
    marginBottom: "1.25rem",
})

const externalLinkStyle = css({
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    padding: "0.6rem 0",
    borderBottom: "1px solid #f3f4f6",
    color: "#2563eb",
    textDecoration: "none",
    fontSize: "0.875rem",
    "&:last-child": {
        borderBottom: "none",
    },
    "&:hover": {
        textDecoration: "underline",
    },
})

export const Intro: FunctionComponent = () => (
    <div css={pageStyle}>
        <div css={innerStyle}>
            <p css={[labelTextStyle, css`
                letter-spacing: 0.12em;
                margin-bottom: 0.75rem;`]}><BsFillLightbulbFill/> Simuleer lokale energiesystemen</p>
            <h1 css={headingStyle}>Welkom bij LUX Dataportaal</h1>

            <p css={sectionDescStyle}>
                LUX Dataportaal is een webapplicatie om energiedata van bedrijven te verzamelen
                en lokale energiesystemen te simuleren met het LUX Energy Twin.
            </p>
            <p css={labelTextStyle}>Wat u kunt vinden op deze website</p>

            <div css={mainGridStyle}>
                <div css={cardsColumnStyle}>
                    {pageCards.map((card) => (
                        <Link key={card.number} to={card.to} css={cardStyle}>
                            <p css={cardNumberStyle}>{card.number}</p>
                            <div css={iconContainerStyle}>{card.icon}</div>
                            <p css={cardTitleStyle}>{card.title}</p>
                            <p css={cardDescStyle}>{card.description}</p>
                            <span css={cardFooterStyle}>
                                Bekijken <BsArrowRight />
                            </span>
                        </Link>
                    ))}
                </div>

                <div css={linksColumnStyle}>
                    <div css={linksPanelStyle}>
                        <p css={linksPanelTitleStyle}>Links naar andere plekken</p>
                        {linksToOtherPlaces.map((link, index) => (
                            <a key={index} href={link.to} target="_blank" rel="noreferrer" css={externalLinkStyle}>
                                <span>{link.title}</span>
                                <BsBoxArrowUpRight style={{flexShrink: 0, marginLeft: "0.5rem", color: "#6b7280"}} />
                            </a>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    </div>
)