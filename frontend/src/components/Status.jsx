// Export the status component as the default export of this module. 
export default function Status({ loading, error }) {

    // if the loading state is true, display a loading message
    // prevents the rest of the component from executing until the data is fetched
    if (loading) return <p>Loading...</p>

    //if the 'error' contains a value, display the error message.
    // the style is set to 'crimson' red to indicate an error state to the user
    if (error) return <p style={{ color: 'crimson' }}>Error: {error}</p>   


    //if the comoponent isnt loading nor experiencing an error, return null to render nothing.
    return null
}