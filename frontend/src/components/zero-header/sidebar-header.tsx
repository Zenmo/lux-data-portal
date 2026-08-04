import {FunctionComponent} from "react"
import {css} from "@emotion/react"
import {BsX} from "react-icons/bs"

export const ZENMO_LOGO = "https://zenmo.com/wp-content/uploads/2018/12/zenmo-logo-website-grey.png"

const sidebarHeaderStyle = css({
    display: "flex",
    alignItems: "center",
    justifyContent: "space-between",
    width: "100%",
    padding: "0.25rem 0",
})

const closeButtonStyle = css({
    border: "1px solid #d1d5db",
    borderRadius: "0.375rem",
    background: "#f3f4f6",
    display: "flex",
    alignItems: "center",
    justifyContent: "center",
    width: "2rem",
    height: "2rem",
    color: "#374151",
    "&:hover": {
        backgroundColor: "#e5e7eb",
    },
})

export const SidebarHeader: FunctionComponent<{onClose: () => void}> = ({onClose}) => (
    <div css={sidebarHeaderStyle}>
        <img src={ZENMO_LOGO} alt="Zenmo logo" css={{height: "2.5rem"}} />
        <button css={closeButtonStyle} onClick={onClose} aria-label="Sluiten">
            <BsX />
        </button>
    </div>
)
